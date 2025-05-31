package com.swens.task_service.service;

import com.swens.task_service.dto.TaskRequestDTO;
import com.swens.task_service.dto.TaskResponseDTO;
import com.swens.task_service.dto.TaskUpdateDTO;
import com.swens.task_service.dto.UserInfoDTO;
import com.swens.task_service.exception.NoAssignedUserException;
import com.swens.task_service.exception.NoUserAvailableException;
import com.swens.task_service.exception.TaskNotFoundException;
import com.swens.task_service.grpc.UserServiceGrpcClient;
import com.swens.task_service.kafka.KafkaProducer;
import com.swens.task_service.mapper.TaskMapper;
import com.swens.task_service.model.Task;
import com.swens.task_service.repository.TaskRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final String NOT_ASSIGNED = "NOT_ASSIGNED";
    private static final String ASSIGNED = "ASSIGNED";

    private final TaskRepository taskRepository;
    private final UserServiceGrpcClient userGrpcClient;
    private final KafkaProducer kafkaProducer;

    // Commented out in-memory maps
    // private final ConcurrentHashMap<String, String> userTaskMap = new ConcurrentHashMap<>();
    // private final ConcurrentHashMap<String, UserInfoDTO> userInfoMap = new ConcurrentHashMap<>();

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOpsStatus;     // For user-task status
    private final HashOperations<String, String, UserInfoDTO> hashOpsUserInfo; // For user info

    private static final String USER_TASK_MAP = "USER_TASK_MAP";
    private static final String USER_INFO_MAP = "USER_INFO_MAP";

    public TaskService(TaskRepository taskRepository,
                       UserServiceGrpcClient userServiceGrpcClient,
                       KafkaProducer kafkaProducer,
                       RedisTemplate<String, Object> redisTemplate) {
        this.taskRepository = taskRepository;
        this.userGrpcClient = userServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
        this.redisTemplate = redisTemplate;
        this.hashOpsStatus = redisTemplate.opsForHash();
        this.hashOpsUserInfo = redisTemplate.opsForHash();
    }

    public List<UserInfoDTO> getUsersByRole(String role) {
        List<UserInfoDTO> users = userGrpcClient.getUsersByRole(role);
        for (UserInfoDTO user : users) {

//            userTaskMap.putIfAbsent(user.getId(), NOT_ASSIGNED);
//            userInfoMap.putIfAbsent(user.getId(), user);

            // Use Redis hash to store status and info
            if (!hashOpsStatus.hasKey(USER_TASK_MAP, user.getId())) {
                hashOpsStatus.put(USER_TASK_MAP, user.getId(), NOT_ASSIGNED);
            }
            if (!hashOpsUserInfo.hasKey(USER_INFO_MAP, user.getId())) {
                hashOpsUserInfo.put(USER_INFO_MAP, user.getId(), user);
            }
        }
        return users;
    }

    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        Task task = TaskMapper.toEntity(requestDTO);
        List<Task.AssignedUser> incomingUsers = task.getAssignedUsers();

        if (incomingUsers == null || incomingUsers.isEmpty()) {
            throw new NoAssignedUserException("Task must have at least one assigned user.");
        }

        List<Task.AssignedUser> assignedUsers = new ArrayList<>();
        List<String> unavailableUsers = new ArrayList<>();

        for (Task.AssignedUser user : incomingUsers) {
//            String status = userTaskMap.get(user.getUserId());

            String status = hashOpsStatus.get(USER_TASK_MAP, user.getUserId());

            if (NOT_ASSIGNED.equals(status)) {
                assignedUsers.add(user);
            } else if (ASSIGNED.equals(status)) {
                unavailableUsers.add(user.getUserId() + " is already assigned to another task");
            } else {
                unavailableUsers.add(user.getUserId() + " is not tracked or unavailable");
            }
        }

        if (assignedUsers.isEmpty()) {
            String msg = "No available users to assign the task. Details: " + String.join(", ", unavailableUsers);
            throw new NoUserAvailableException(msg);
        }

        // Assign all available users
        task.setAssignedUsers(assignedUsers);

        Task saved = taskRepository.save(task);

        // Mark all assigned users in Redis
        for (Task.AssignedUser user : assignedUsers) {
//            userTaskMap.put(user.getUserId(), ASSIGNED);
            hashOpsStatus.put(USER_TASK_MAP, user.getUserId(), ASSIGNED);
        }

        kafkaProducer.sendTaskCreatedEvent(saved);

        TaskResponseDTO responseDTO = TaskMapper.toDTO(saved);
        responseDTO.setUnavailableUsers(unavailableUsers);

        return responseDTO;
    }

    public TaskResponseDTO updateTask(String taskId, TaskUpdateDTO updateDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        List<Task.AssignedUser> oldAssignedUsers = new ArrayList<>(task.getAssignedUsers());

        String updatedStatus = updateDTO.getStatus() != null ? updateDTO.getStatus().toLowerCase() : "";

        TaskMapper.updateEntity(task, updateDTO);

        // If completed, mark users as NOT_ASSIGNED but keep them in the task for history
        if (updatedStatus.equals("completed")) {
            for (Task.AssignedUser oldUser : oldAssignedUsers) {
//                userTaskMap.put(oldUser.getUserId(), NOT_ASSIGNED);
                hashOpsStatus.put(USER_TASK_MAP, oldUser.getUserId(), NOT_ASSIGNED);
            }
            // DO NOT clear assignedUsers
        }

        Task updated = taskRepository.save(task);

        if (!updatedStatus.equals("completed")) {
            List<Task.AssignedUser> newAssignedUsers = updated.getAssignedUsers();

            for (Task.AssignedUser user : newAssignedUsers) {
//                userTaskMap.put(user.getUserId(), ASSIGNED);
                hashOpsStatus.put(USER_TASK_MAP, user.getUserId(), ASSIGNED);
            }

            for (Task.AssignedUser oldUser : oldAssignedUsers) {
                boolean stillAssigned = newAssignedUsers.stream()
                        .anyMatch(newUser -> newUser.getUserId().equals(oldUser.getUserId()));
                if (!stillAssigned) {
//                    userTaskMap.put(oldUser.getUserId(), NOT_ASSIGNED);
                    hashOpsStatus.put(USER_TASK_MAP, oldUser.getUserId(), NOT_ASSIGNED);
                }
            }
        }

        kafkaProducer.sendTaskUpdatedEvent(updated);

        return TaskMapper.toDTO(updated);
    }

    public TaskResponseDTO getTaskById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return TaskMapper.toDTO(task);
    }

    public void deleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        for (Task.AssignedUser user : task.getAssignedUsers()) {
//            userTaskMap.put(user.getUserId(), NOT_ASSIGNED);
            hashOpsStatus.put(USER_TASK_MAP, user.getUserId(), NOT_ASSIGNED);
        }

        taskRepository.deleteById(taskId);
    }

    public List<UserInfoDTO> getFreeUsers() {

//        return userTaskMap.entrySet().stream()
//                .filter(entry -> NOT_ASSIGNED.equals(entry.getValue()))
//                .map(entry -> userInfoMap.get(entry.getKey()))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());


        return hashOpsStatus.entries(USER_TASK_MAP).entrySet().stream()
                .filter(entry -> NOT_ASSIGNED.equals(entry.getValue()))
                .map(entry -> hashOpsUserInfo.get(USER_INFO_MAP, entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getAssignedUsers() {

//        return userTaskMap.entrySet().stream()
//                .filter(entry -> ASSIGNED.equals(entry.getValue()))
//                .map(entry -> userInfoMap.get(entry.getKey()))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());

        return hashOpsStatus.entries(USER_TASK_MAP).entrySet().stream()
                .filter(entry -> ASSIGNED.equals(entry.getValue()))
                .map(entry -> hashOpsUserInfo.get(USER_INFO_MAP, entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }
}

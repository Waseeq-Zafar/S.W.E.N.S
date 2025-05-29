package com.swens.task_service.service;

import com.swens.task_service.dto.TaskRequestDTO;
import com.swens.task_service.dto.TaskResponseDTO;
import com.swens.task_service.dto.TaskUpdateDTO;
import com.swens.task_service.dto.UserInfoDTO;
import com.swens.task_service.exception.TaskNotFoundException;
import com.swens.task_service.grpc.UserServiceGrpcClient;
import com.swens.task_service.mapper.TaskMapper;
import com.swens.task_service.model.Task;
import com.swens.task_service.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final String NOT_ASSIGNED = "NOT_ASSIGNED";
    private static final String ASSIGNED = "ASSIGNED";

    private final TaskRepository taskRepository;
    private final UserServiceGrpcClient userGrpcClient;

    private final ConcurrentHashMap<String, String> userTaskMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserInfoDTO> userInfoMap = new ConcurrentHashMap<>();

    public TaskService(TaskRepository taskRepository, UserServiceGrpcClient userServiceGrpcClient) {
        this.taskRepository = taskRepository;
        this.userGrpcClient = userServiceGrpcClient;
    }

    public List<UserInfoDTO> getUsersByRole(String role) {
        List<UserInfoDTO> users = userGrpcClient.getUsersByRole(role);
        for (UserInfoDTO user : users) {
            userTaskMap.putIfAbsent(user.getId(), NOT_ASSIGNED);
            userInfoMap.putIfAbsent(user.getId(), user); // Store full user info
        }
        return users;
    }

    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        Task task = TaskMapper.toEntity(requestDTO);
        Task saved = taskRepository.save(task);

        for (Task.AssignedUser user : saved.getAssignedUsers()) {
            userTaskMap.put(user.getUserId(), ASSIGNED);
        }

        return TaskMapper.toDTO(saved);
    }

    public TaskResponseDTO updateTask(String taskId, TaskUpdateDTO updateDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        List<Task.AssignedUser> oldAssignedUsers = new ArrayList<>(task.getAssignedUsers());

        String updatedStatus = updateDTO.getStatus() != null ? updateDTO.getStatus().toLowerCase() : "";

        TaskMapper.updateEntity(task, updateDTO);

        // If completed, mark users as NOT_ASSIGNED, but keep them in the task for history
        if (updatedStatus.equals("completed")) {
            for (Task.AssignedUser oldUser : oldAssignedUsers) {
                userTaskMap.put(oldUser.getUserId(), NOT_ASSIGNED);
            }
            // DO NOT clear assignedUsers
        }

        Task updated = taskRepository.save(task);

        if (!updatedStatus.equals("completed")) {
            List<Task.AssignedUser> newAssignedUsers = updated.getAssignedUsers();

            for (Task.AssignedUser user : newAssignedUsers) {
                userTaskMap.put(user.getUserId(), ASSIGNED);
            }

            for (Task.AssignedUser oldUser : oldAssignedUsers) {
                boolean stillAssigned = newAssignedUsers.stream()
                        .anyMatch(newUser -> newUser.getUserId().equals(oldUser.getUserId()));
                if (!stillAssigned) {
                    userTaskMap.put(oldUser.getUserId(), NOT_ASSIGNED);
                }
            }
        }

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
            userTaskMap.put(user.getUserId(), NOT_ASSIGNED);
        }

        taskRepository.deleteById(taskId);
    }

    public List<UserInfoDTO> getFreeUsers() {
        return userTaskMap.entrySet().stream()
                .filter(entry -> NOT_ASSIGNED.equals(entry.getValue()))
                .map(entry -> userInfoMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getAssignedUsers() {
        return userTaskMap.entrySet().stream()
                .filter(entry -> ASSIGNED.equals(entry.getValue()))
                .map(entry -> userInfoMap.get(entry.getKey()))
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

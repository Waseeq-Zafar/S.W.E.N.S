package com.swens.task_service.controller;

import com.swens.task_service.dto.TaskRequestDTO;
import com.swens.task_service.dto.TaskResponseDTO;
import com.swens.task_service.dto.TaskUpdateDTO;
import com.swens.task_service.dto.UserInfoDTO;
import com.swens.task_service.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 1. Get users by role via gRPC
    @GetMapping("/role")
    public ResponseEntity<List<UserInfoDTO>> getUsersByRole(@RequestParam String role) {
        List<UserInfoDTO> users = taskService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    // 2. Create a task
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO requestDTO) {
        TaskResponseDTO created = taskService.createTask(requestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getTaskId()) // assuming getId() returns the created task's ID
                .toUri();

        return ResponseEntity.created(location).body(created);
    }


    // 3. Update task
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable String taskId,
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        TaskResponseDTO updated = taskService.updateTask(taskId, updateDTO);
        return ResponseEntity.ok(updated);
    }

    // 4. Get a task by ID
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable String taskId) {
        TaskResponseDTO dto = taskService.getTaskById(taskId);
        return ResponseEntity.ok(dto);
    }

    // 5. Delete task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<TaskResponseDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    @GetMapping("/free")
    public ResponseEntity<List<UserInfoDTO>> getFreeUsers() {
        List<UserInfoDTO> freeUsers = taskService.getFreeUsers();
        return ResponseEntity.ok(freeUsers);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<UserInfoDTO>> getAssignedUsers() {
        List<UserInfoDTO> assignedUsers = taskService.getAssignedUsers();
        return ResponseEntity.ok(assignedUsers);
    }

}

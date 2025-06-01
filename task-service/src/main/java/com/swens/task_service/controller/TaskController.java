package com.swens.task_service.controller;

import com.swens.task_service.dto.*;
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

    // ========== ADMIN METHODS ==========

    @GetMapping("/admin/role")
    public ResponseEntity<List<UserInfoDTO>> getUsersByRole(@RequestParam String role) {
        List<UserInfoDTO> users = taskService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/admin")
    public ResponseEntity<TaskResponseDTO> createTask(@RequestHeader("X-EMAIL") String email,
                                                      @Valid @RequestBody TaskRequestDTO requestDTO) {
        TaskResponseDTO created = taskService.createTask(email ,requestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getTaskId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/admin/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable String taskId,
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        TaskResponseDTO updated = taskService.updateTask(taskId, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/admin/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable String taskId) {
        TaskResponseDTO dto = taskService.getTaskById(taskId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/admin/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<TaskResponseDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/admin/free")
    public ResponseEntity<List<UserInfoDTO>> getFreeUsers() {
        List<UserInfoDTO> freeUsers = taskService.getFreeUsers();
        return ResponseEntity.ok(freeUsers);
    }

    @GetMapping("/admin/assigned")
    public ResponseEntity<List<UserInfoDTO>> getAssignedUsers() {
        List<UserInfoDTO> assignedUsers = taskService.getAssignedUsers();
        return ResponseEntity.ok(assignedUsers);
    }


    // ========== USER METHODS ==========

    // Use email from the header (X-EMAIL), no path variable needed
    @GetMapping("/user")
    public ResponseEntity<List<TaskUserInfoDTO>> getUserWithTasks(@RequestHeader("X-EMAIL") String email) {
        List<TaskUserInfoDTO> userInfo = taskService.getUserTasks(email);
        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/user/{taskId}")
    public ResponseEntity<TaskUserInfoDTO> updateUserTask(
            @PathVariable String taskId,
            @Valid @RequestBody TaskUserUpdateDTO updateDTO) {
        TaskUserInfoDTO userInfoDTO = taskService.updateUserTasks(taskId, updateDTO);
        return ResponseEntity.ok(userInfoDTO);
    }

}

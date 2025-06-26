package com.swens.task_service.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTaskNotFoundException(TaskNotFoundException tx) {
        log.warn("task not found {}", tx.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Task not found");
        return ResponseEntity.status(404).body(errors); // ✅ Use 404 status
    }

    @ExceptionHandler(NoUserAvailableException.class)
    public ResponseEntity<Map<String, String>> handleNoUserAvailableException(NoUserAvailableException ua) {
        log.warn("no user available {}", ua.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "User not Available");
        return ResponseEntity.status(404).body(errors); // ✅ Use 404 status
    }

    @ExceptionHandler(NoAssignedUserException.class)
    public ResponseEntity<Map<String, String>> handleNoAssignedUserException(NoAssignedUserException ua) {
        log.warn("no assigned user {}", ua.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "User not Assigned to the Task");
        return ResponseEntity.status(404).body(errors); // ✅ Use 404 status
    }



}

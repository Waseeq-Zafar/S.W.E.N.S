package com.swens.task_service.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) { super(message); }
}

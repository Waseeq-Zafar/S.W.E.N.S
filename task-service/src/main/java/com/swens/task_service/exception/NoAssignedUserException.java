package com.swens.task_service.exception;

public class NoAssignedUserException extends RuntimeException{
    public NoAssignedUserException(String message) { super(message); }
}

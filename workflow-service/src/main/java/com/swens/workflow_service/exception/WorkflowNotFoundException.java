package com.swens.workflow_service.exception;

public class WorkflowNotFoundException extends RuntimeException{
    public WorkflowNotFoundException(String message) { super(message); }
}

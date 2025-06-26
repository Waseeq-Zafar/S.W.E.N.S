package com.swens.task_service.dto;

public class TaskUserUpdateDTO {
    private String status;

    private String description;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

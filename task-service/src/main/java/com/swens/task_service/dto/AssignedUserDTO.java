package com.swens.task_service.dto;


import jakarta.validation.constraints.NotBlank;

public class AssignedUserDTO {

    private String userId;
    private String userName;

    public AssignedUserDTO(@NotBlank(message = "User ID is required") String userId, @NotBlank(message = "User name is required") String userName) {
        this.userId = userId;
        this.userName = userName;
    }


    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

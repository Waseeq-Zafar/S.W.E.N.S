package com.swens.task_service.dto;


import jakarta.validation.constraints.NotBlank;

public class AssignedUserDTO {

    private String userId;
    private String userName;
    private String email;


    public AssignedUserDTO(
            @NotBlank(message = "User ID is required") String userId,
            @NotBlank(message = "User name is required") String userName,
            @NotBlank(message = "Email is required") String email
    ) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
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

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

}

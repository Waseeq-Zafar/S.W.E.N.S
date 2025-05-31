package com.swens.auth_service.dto;

public class UserCacheDTO {
    private String email;
    private String name;
    private String hashedPassword;
    private String role;

    public UserCacheDTO (String email, String name, String hashedPassword, String role) {
        this.email = email;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

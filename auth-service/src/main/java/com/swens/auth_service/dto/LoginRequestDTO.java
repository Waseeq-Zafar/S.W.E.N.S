package com.swens.auth_service.dto;

public class LoginRequestDTO {
    private String email;
    private String password;

    // Getters and setters (or use Lombok @Data)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

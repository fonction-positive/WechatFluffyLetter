package com.fluffyletter.dto;

public class AdminLoginResponse {

    private String token;
    private String role;

    public AdminLoginResponse() {
    }

    public AdminLoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}

package com.fluffyletter.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminAdminUserUpdateRoleRequest {

    @NotBlank
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

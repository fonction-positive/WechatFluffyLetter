package com.fluffyletter.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminAdminUserResetPasswordRequest {

    @NotBlank
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

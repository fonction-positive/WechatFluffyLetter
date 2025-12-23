package com.fluffyletter.service;

public class AdminContext {

    private final Long adminId;
    private final String role;

    public AdminContext(Long adminId, String role) {
        this.adminId = adminId;
        this.role = role;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getRole() {
        return role;
    }

    public boolean isSuperAdmin() {
        return "superadmin".equalsIgnoreCase(role);
    }
}

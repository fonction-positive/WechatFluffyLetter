package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminAdminUserCreateRequest;
import com.fluffyletter.dto.AdminAdminUserDTO;
import com.fluffyletter.dto.AdminAdminUserResetPasswordRequest;
import com.fluffyletter.dto.AdminAdminUserUpdateRoleRequest;
import com.fluffyletter.entity.AdminUser;
import com.fluffyletter.repository.AdminUserRepository;
import com.fluffyletter.service.AdminAuthService;
import com.fluffyletter.service.AuthHeaderService;
import com.fluffyletter.service.ForbiddenException;
import com.fluffyletter.service.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/admin-users")
public class AdminAdminUserController {

    private final AuthHeaderService authHeaderService;
    private final AdminUserRepository adminUserRepository;
    private final AdminAuthService adminAuthService;

    public AdminAdminUserController(AuthHeaderService authHeaderService,
                                   AdminUserRepository adminUserRepository,
                                   AdminAuthService adminAuthService) {
        this.authHeaderService = authHeaderService;
        this.adminUserRepository = adminUserRepository;
        this.adminAuthService = adminAuthService;
    }

    private void requireSuperAdmin(String authorizationHeader) {
        var ctx = authHeaderService.requireAdmin(authorizationHeader);
        if (!ctx.isSuperAdmin()) {
            throw new ForbiddenException("superadmin required");
        }
    }

    @GetMapping
    public List<AdminAdminUserDTO> list(@RequestHeader("Authorization") String authorization) {
        requireSuperAdmin(authorization);
        var sort = Sort.by(Sort.Order.asc("id"));
        return adminUserRepository.findAll(sort).stream()
                .map(u -> new AdminAdminUserDTO(u.getId(), u.getUsername(), u.getRole()))
                .toList();
    }

    @PostMapping
    public AdminAdminUserDTO create(@RequestHeader("Authorization") String authorization,
                                    @Valid @RequestBody AdminAdminUserCreateRequest request) {
        requireSuperAdmin(authorization);

        String username = request.getUsername().trim();
        String password = request.getPassword();
        String role = request.getRole().trim();

        if (username.length() > 50) {
            throw new IllegalArgumentException("username too long");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("password is required");
        }
        if (role.length() > 20) {
            throw new IllegalArgumentException("role too long");
        }
        if (adminUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("username already exists");
        }

        AdminUser u = new AdminUser();
        u.setUsername(username);
        u.setPasswordHash(adminAuthService.encoder().encode(password));
        u.setRole(role);
        u = adminUserRepository.save(u);
        return new AdminAdminUserDTO(u.getId(), u.getUsername(), u.getRole());
    }

    @PutMapping("/{id}/role")
    public AdminAdminUserDTO updateRole(@RequestHeader("Authorization") String authorization,
                                        @PathVariable Long id,
                                        @Valid @RequestBody AdminAdminUserUpdateRoleRequest request) {
        var ctx = authHeaderService.requireAdmin(authorization);
        if (!ctx.isSuperAdmin()) {
            throw new ForbiddenException("superadmin required");
        }
        if (ctx.getAdminId().equals(id)) {
            throw new ForbiddenException("cannot change your own role");
        }

        AdminUser u = adminUserRepository.findById(id).orElseThrow(() -> new NotFoundException("admin user not found"));
        String role = request.getRole().trim();
        if (role.length() > 20) {
            throw new IllegalArgumentException("role too long");
        }
        u.setRole(role);
        u = adminUserRepository.save(u);
        return new AdminAdminUserDTO(u.getId(), u.getUsername(), u.getRole());
    }

    @PutMapping("/{id}/password")
    public void resetPassword(@RequestHeader("Authorization") String authorization,
                              @PathVariable Long id,
                              @Valid @RequestBody AdminAdminUserResetPasswordRequest request) {
        requireSuperAdmin(authorization);
        AdminUser u = adminUserRepository.findById(id).orElseThrow(() -> new NotFoundException("admin user not found"));
        u.setPasswordHash(adminAuthService.encoder().encode(request.getPassword()));
        adminUserRepository.save(u);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String authorization,
                       @PathVariable Long id) {
        var ctx = authHeaderService.requireAdmin(authorization);
        if (!ctx.isSuperAdmin()) {
            throw new ForbiddenException("superadmin required");
        }
        if (ctx.getAdminId().equals(id)) {
            throw new ForbiddenException("cannot delete yourself");
        }
        if (!adminUserRepository.existsById(id)) {
            throw new NotFoundException("admin user not found");
        }
        adminUserRepository.deleteById(id);
    }
}

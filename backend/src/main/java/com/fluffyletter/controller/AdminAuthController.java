package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminLoginRequest;
import com.fluffyletter.dto.AdminLoginResponse;
import com.fluffyletter.entity.AdminUser;
import com.fluffyletter.service.AdminAuthService;
import com.fluffyletter.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final JwtService jwtService;

    public AdminAuthController(AdminAuthService adminAuthService, JwtService jwtService) {
        this.adminAuthService = adminAuthService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        AdminUser user = adminAuthService.requireValidLogin(request.getUsername(), request.getPassword());
        String token = jwtService.issueAdminToken(user.getId(), user.getRole());
        return new AdminLoginResponse(token, user.getRole());
    }
}

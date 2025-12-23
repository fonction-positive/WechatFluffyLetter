package com.fluffyletter.service;

import com.fluffyletter.entity.AdminUser;
import com.fluffyletter.repository.AdminUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminAuthService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    public AdminUser requireValidLogin(String username, String password) {
        AdminUser user = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("invalid username or password"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("invalid username or password");
        }

        return user;
    }

    public BCryptPasswordEncoder encoder() {
        return encoder;
    }
}

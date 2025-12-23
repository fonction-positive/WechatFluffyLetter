package com.fluffyletter.bootstrap;

import com.fluffyletter.config.FluffyProperties;
import com.fluffyletter.entity.AdminUser;
import com.fluffyletter.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final FluffyProperties props;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminBootstrapRunner(AdminUserRepository adminUserRepository, FluffyProperties props) {
        this.adminUserRepository = adminUserRepository;
        this.props = props;
    }

    @Override
    public void run(String... args) {
        var bootstrap = props.getAdmin().getBootstrap();

        if (adminUserRepository.count() > 0) {
            return;
        }

        if (!StringUtils.hasText(bootstrap.getUsername()) || !StringUtils.hasText(bootstrap.getPassword())) {
            return;
        }

        var admin = new AdminUser();
        admin.setUsername(bootstrap.getUsername().trim());
        admin.setPasswordHash(passwordEncoder.encode(bootstrap.getPassword()));
        admin.setRole(StringUtils.hasText(bootstrap.getRole()) ? bootstrap.getRole().trim() : "superadmin");

        adminUserRepository.save(admin);
    }
}

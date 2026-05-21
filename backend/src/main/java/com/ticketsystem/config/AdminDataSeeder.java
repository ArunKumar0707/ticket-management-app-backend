package com.ticketsystem.config;

import com.ticketsystem.entity.Role;
import com.ticketsystem.entity.User;
import com.ticketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedDefaultAdmin();
    }

    private void seedDefaultAdmin() {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.ADMIN);

        if (adminExists) {
            log.info("Admin user already exists — skipping seed.");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@nexus.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);
        log.info("Default admin user seeded: admin@nexus.com / admin123");
    }
}

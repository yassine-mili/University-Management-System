package com.universite.auth.config;

import com.universite.auth.dto.RegisterRequest;
import com.universite.auth.model.Role;
import com.universite.auth.repository.UserRepository;
import com.universite.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataInitializer implements ApplicationRunner {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${app.sample-data.enabled:true}")
    private boolean sampleDataEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!sampleDataEnabled) {
            return;
        }

        final String demoEmail = "student.demo@university.com";
        if (userRepository.existsByEmail(demoEmail)) {
            log.debug("Sample student user already exists, skipping seeding");
            return;
        }

        try {
            authService.register(
                RegisterRequest.builder()
                    .username("demo.student")
                    .email(demoEmail)
                    .password("DemoPass123!")
                    .role(Role.STUDENT)
                    .build()
            );
            log.info("Sample student account created (username: demo.student / password: DemoPass123!)");
        } catch (Exception ex) {
            log.warn("Unable to seed sample student account", ex);
        }
    }
}

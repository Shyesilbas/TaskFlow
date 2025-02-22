package com.serhat.taskFlow.component;

import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.enums.MembershipPlan;
import com.serhat.taskFlow.entity.enums.Role;
import com.serhat.taskFlow.repository.AdminRepository;
import com.serhat.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInit {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (adminRepository.findByUsername("admin").isEmpty()) {
                Admin admin = Admin.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .phone("1234567890")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                adminRepository.save(admin);
                System.out.println("Default admin created: username=admin, password=admin123");
            } else {
                System.out.println("Admin already exists, skipping initialization.");
            } if(userRepository.findByUsername("user").isEmpty()){
                AppUser user = AppUser.builder()
                        .username("user")
                        .email("user@gmail.com")
                        .phone("112233")
                        .password(passwordEncoder.encode("User123."))
                        .role(Role.CUSTOMER)
                        .membershipPlan(MembershipPlan.BASIC)
                        .build();
                userRepository.save(user);
                System.out.println("Default admin created: username=user, password=User123.");
            }
            else {
                System.out.println("Admin already exists, skipping initialization.");
            }
        };
    }
}

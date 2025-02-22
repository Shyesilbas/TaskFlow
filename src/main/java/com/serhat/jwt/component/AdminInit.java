package com.serhat.jwt.component;

import com.serhat.jwt.entity.Admin;
import com.serhat.jwt.entity.AppUser;
import com.serhat.jwt.entity.enums.MembershipPlan;
import com.serhat.jwt.entity.enums.Role;
import com.serhat.jwt.repository.AdminRepository;
import com.serhat.jwt.repository.UserRepository;
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

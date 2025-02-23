package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.requests.AssignRequest;
import com.serhat.taskFlow.dto.responses.AssignSuccessful;
import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.exception.UserAlreadyAssignedToAdminException;
import com.serhat.taskFlow.interfaces.AdminInterface;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.repository.AdminRepository;
import com.serhat.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssigningService {

    private final AdminInterface adminInterface;
    private final AdminRepository adminRepository;
    private final UserInterface userInterface;
    private final UserRepository userRepository;

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public AssignSuccessful assignAdminToUser(AssignRequest assignRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String managerUsername = userDetails.getUsername();
        Long adminId = assignRequest.adminId();
        Long userId = assignRequest.userId();
        log.info("Manager {} is attempting to assign admin ID: {} to user ID: {}", managerUsername, adminId, userId);

        AppUser user = userInterface.findById(userId);
        Admin admin = adminInterface.findById(adminId);

        if (user.getAdmin() != null && !user.getAdmin().getAdminId().equals(adminId)) {
            log.warn("User ID: {} is already assigned to admin ID: {}. Assignment rejected.", userId, user.getAdmin().getAdminId());
            throw new UserAlreadyAssignedToAdminException("User is already assigned to another admin.");
        }

        user.setAdmin(admin);
        userRepository.save(user);

        log.info("Manager {} successfully assigned admin {} (ID: {}) to user {} (ID: {})",
                managerUsername, admin.getUsername(), admin.getAdminId(), user.getUsername(), user.getUserId());

        return new AssignSuccessful(
                "Admin successfully assigned to user",
                userId,
                adminId,
                LocalDateTime.now()
        );
    }
}
package com.serhat.taskFlow.service;

import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Notification;
import com.serhat.taskFlow.entity.enums.NotificationType;
import com.serhat.taskFlow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotificationToUser(AppUser recipientUser, NotificationType notificationType, String message) {

        Notification notification = Notification.builder()
                .recipientUser(recipientUser)
                .message(message)
                .type(notificationType)
                .recipientManager(null)
                .recipientAdmin(null)
                .build();

        notificationRepository.save(notification);
        log.info("User {} notified with message: {}", recipientUser.getUsername(), message);
    }

    public void sendNotificationToAdmin(Admin recipientAdmin, NotificationType notificationType ,String message) {

        Notification notification = Notification.builder()
                .recipientAdmin(recipientAdmin)
                .message(message)
                .type(notificationType)
                .recipientUser(null)
                .recipientManager(null)
                .build();

        notificationRepository.save(notification);
        log.info("Admin {} notified with message: {}", recipientAdmin.getUsername(), message);
    }

}

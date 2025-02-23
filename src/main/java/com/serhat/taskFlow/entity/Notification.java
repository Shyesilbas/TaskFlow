package com.serhat.taskFlow.entity;

import com.serhat.taskFlow.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private LocalDateTime createdAt;

    @PrePersist
    public void initNotification(){
        this.createdAt=LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "recipient_user_id")
    private AppUser recipientUser;

    @ManyToOne
    @JoinColumn(name = "recipient_admin_id")
    private Admin recipientAdmin;

    @ManyToOne
    @JoinColumn(name = "recipient_manager_id")
    private Manager recipientManager;

}

package com.serhat.taskFlow.mapper;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.AdminMultipleTaskRequest;
import com.serhat.taskFlow.dto.requests.AdminTaskRequest;
import com.serhat.taskFlow.dto.requests.UserTaskRequest;
import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.interfaces.DateRangeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TaskMapper {
    private final DateRangeParser dateRangeParser;

    public TaskDto toTaskDto(Task task){
        return TaskDto.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .assignedBy(task.getAssignedBy() != null ? task.getAssignedBy().getUsername() : null)
                .description(task.getDescription())
                .status(task.getStatus().name())
                .assignedToUsername(task.getAssignedTo().getUsername())
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDueDate())
                .keywords(task.getKeywords())
                .taskPriority(task.getTaskPriority())
                .adminComment(task.getAdminComment())
                .userComment(task.getUserComment())
                .build();
    }

    public Task toUserTaskEntity(UserTaskRequest userTaskRequest, AppUser currentUser){
        return Task.builder()
                .title(userTaskRequest.title())
                .description(userTaskRequest.description())
                .status(TaskStatus.valueOf(userTaskRequest.status()))
                .dueDate(userTaskRequest.dueDate() != null ?
                        dateRangeParser.parseStartDate(userTaskRequest.dueDate()) : null)
                .assignedTo(currentUser)
                .assignedBy(null)
                .keywords(userTaskRequest.keywords())
                .build();
    }

    public Task toAdminTaskEntity(AdminTaskRequest adminTaskRequest, AppUser currentUser , Admin admin){
        return Task.builder()
                .title(adminTaskRequest.title())
                .description(adminTaskRequest.description())
                .status(TaskStatus.valueOf(adminTaskRequest.status()))
                .dueDate(adminTaskRequest.dueDate() != null ?
                        dateRangeParser.parseStartDate(adminTaskRequest.dueDate()) : null)
                .assignedBy(admin)
                .assignedTo(currentUser)
                .keywords(adminTaskRequest.keywords())
                .taskPriority(adminTaskRequest.taskPriority())
                .adminComment(adminTaskRequest.adminComment())
                .userComment(null)
                .build();
    }

    public Task toAdminTaskEntity(AdminMultipleTaskRequest multipleTaskRequest, AppUser currentUser, Admin admin) {
        return Task.builder()
                .title(multipleTaskRequest.title())
                .description(multipleTaskRequest.description())
                .status(TaskStatus.valueOf(multipleTaskRequest.status()))
                .dueDate(multipleTaskRequest.dueDate() != null ?
                        dateRangeParser.parseStartDate(multipleTaskRequest.dueDate()) : null)
                .assignedBy(admin)
                .assignedTo(currentUser)
                .keywords(multipleTaskRequest.keywords())
                .taskPriority(multipleTaskRequest.taskPriority())
                .adminComment(multipleTaskRequest.adminComment())
                .userComment(null)
                .build();
    }

}

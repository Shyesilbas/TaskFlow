package com.serhat.jwt.mapper;

import com.serhat.jwt.dto.objects.TaskDto;
import com.serhat.jwt.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {


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
                .build();
    }

}

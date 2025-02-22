package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.dto.requests.UserTaskRequest;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.TaskNotFoundException;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.TaskRepository;
import com.serhat.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto createTask(UserTaskRequest taskRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        log.info("Creating task for user: {}", username);

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Task task = Task.builder()
                .title(taskRequest.title())
                .description(taskRequest.description())
                .status(TaskStatus.valueOf(taskRequest.status()))
                .dueDate(taskRequest.dueDate() != null ?
                        LocalDateTime.parse(taskRequest.dueDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .assignedTo(currentUser)
                .assignedBy(null)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {} for user: {}", savedTask.getTaskId(), username);
        return taskMapper.toTaskDto(savedTask);
    }

    public List<TaskDto> tasksICreated() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        log.info("Fetching tasks created by user: {}", username);

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Task> tasksICreated = taskRepository.findByAssignedToAndAssignedByIsNull(user);
        log.debug("User fetched {} tasks they created", tasksICreated.size());

        return tasksICreated.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> tasksAssignedToMe() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            log.info("Fetching tasks assigned to user: {}", username);

            AppUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            List<Task> tasksAssignedToMe = taskRepository.findByAssignedTo(user);
            log.debug("User fetched {} tasks assigned to them", tasksAssignedToMe.size());

            return tasksAssignedToMe.stream()
                    .map(taskMapper::toTaskDto)
                    .toList();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskRequest updateTaskRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));

        log.info("Updating task with ID: {} by user: {}", taskId, username);

        if (!existingTask.getAssignedTo().getUsername().equals(username)) {
            log.warn("User {} attempted to update task {} which they don't own", username, taskId);
            throw new AccessDeniedException("You can only update your own tasks");
        }

        existingTask.setTitle(updateTaskRequest.title());
        existingTask.setDescription(updateTaskRequest.description());
        existingTask.setStatus(TaskStatus.valueOf(updateTaskRequest.status()));
        existingTask.setDueDate(updateTaskRequest.dueDate());

        Task updated = taskRepository.save(existingTask);
        log.info("Task with ID: {} updated successfully", taskId);

        return taskMapper.toTaskDto(updated);
    }

    @Transactional
    public String deleteTask(Long taskId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));

        log.info("Deleting task with ID: {} by user: {}", taskId, username);

        if (!task.getAssignedTo().getUsername().equals(username)) {
            log.warn("User {} attempted to delete task {} which they don't own", username, taskId);
            throw new AccessDeniedException("You can only delete your own tasks");
        }

        taskRepository.deleteById(taskId);
        log.info("Task with ID: {} deleted successfully", taskId);

        return "Task with ID: " + taskId + " deleted successfully";
    }
}
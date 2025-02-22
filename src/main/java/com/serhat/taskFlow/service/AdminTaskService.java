package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.AdminTaskRequest;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.TaskNotFoundException;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.AdminRepository;
import com.serhat.taskFlow.repository.TaskRepository;
import com.serhat.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminTaskService {

    private final TaskRepository taskRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto createTask(AdminTaskRequest adminTaskRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        log.info("Admin creating task for user: {}", username);

        Admin currentAdmin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found: " + username));

        AppUser assignedUser = userRepository.findById(adminTaskRequest.assignedTo())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + adminTaskRequest.assignedTo()));

        Task task = Task.builder()
                .title(adminTaskRequest.title())
                .description(adminTaskRequest.description())
                .status(TaskStatus.valueOf(adminTaskRequest.status()))
                .dueDate(adminTaskRequest.dueDate() != null ?
                        LocalDateTime.parse(adminTaskRequest.dueDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .assignedBy(currentAdmin)
                .assignedTo(assignedUser)
                .build();

            log.info("Admin {} assigned task to user ID: {}", currentAdmin.getUsername() , adminTaskRequest.assignedTo());

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {} for user: {}", savedTask.getTaskId(), task.getAssignedTo().getUsername());
        return taskMapper.toTaskDto(task);
    }

    public TaskDto getTaskById(Long taskId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        log.info("Admin fetching specific task by id {} for user: {}", taskId ,username);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new TaskNotFoundException("Task not found by id : "+taskId));

        log.debug("Admin fetched the task : task Id {}",taskId);
        return taskMapper.toTaskDto(task);
    }


    public List<TaskDto> getTasksAssignedToUser(Long userId){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        log.info("Admin {} fetching specific tasks assigned to user for user: {}" ,username,userId);

        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found by id : "+userId));

        List<Task> userTasks = appUser.getTasks();

        return userTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> getTasksYouAssigned(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        log.info("Admin {} fetching tasks that they assigned to users" ,username);

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found : "+username));

        List<Task> adminTasks = admin.getTasks();

        return adminTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskRequest updateTaskRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));

        log.info("Admin updating task with ID: {} by user: {}", taskId, username);

        existingTask.setTitle(updateTaskRequest.title());
        existingTask.setDescription(updateTaskRequest.description());
        existingTask.setStatus(TaskStatus.valueOf(updateTaskRequest.status()));
        existingTask.setDueDate(updateTaskRequest.dueDate());

        Task updated = taskRepository.save(existingTask);
        log.info("Task with ID: {} updated successfully by admin", taskId);

        return taskMapper.toTaskDto(updated);
    }

    @Transactional
    public String deleteTask(Long taskId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));

        log.info("Admin deleting task with ID: {} by user: {}", taskId, username);

        taskRepository.delete(task);
        log.info("Task with ID: {} deleted successfully by admin", taskId);
        return "Task with id : "+taskId+" deleted successfully";
    }
}
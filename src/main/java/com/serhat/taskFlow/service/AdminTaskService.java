package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.AppUserDto;
import com.serhat.taskFlow.dto.objects.NotificationDto;
import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.objects.UserTaskStatsDto;
import com.serhat.taskFlow.dto.requests.AdminMultipleTaskRequest;
import com.serhat.taskFlow.dto.requests.AdminTaskRequest;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Notification;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.NotificationType;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.TaskCannotBeAssignedException;
import com.serhat.taskFlow.interfaces.AdminInterface;
import com.serhat.taskFlow.interfaces.DateRangeParser;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AdminTaskService extends BaseTaskService {

    public AdminTaskService(TaskRepository taskRepository, TaskMapper taskMapper, DateRangeParser dateRangeParser,
                            UserInterface userInterface, AdminInterface adminInterface, NotificationService notificationService) {
        super(taskRepository, taskMapper, dateRangeParser, userInterface, adminInterface, notificationService);
    }

    @Transactional
    public TaskDto createTask(AdminTaskRequest adminTaskRequest) {
        String username = getCurrentUsername();
        log.info("Admin creating task for user: {}", username);

        Admin currentAdmin = getCurrentAdmin();
        AppUser assignedUser = userInterface.findById(adminTaskRequest.assignedTo());

        if (!assignedUser.getAdmin().getAdminId().equals(currentAdmin.getAdminId())) {
            throw new TaskCannotBeAssignedException("You can only assign tasks to users related to you");
        }

        Task task = taskMapper.toAdminTaskEntity(adminTaskRequest, assignedUser, currentAdmin);

        log.info("Admin {} assigned task to user ID: {}", currentAdmin.getUsername(), adminTaskRequest.assignedTo());

        Task savedTask = taskRepository.save(task);
        notificationService.sendNotificationToAdmin(currentAdmin, NotificationType.TASK_ASSIGNED,
                "Task '" + savedTask.getTitle() + "' (ID: " + savedTask.getTaskId() + ") assigned to user " + assignedUser.getUsername());
        log.info("Task created successfully with ID: {} for user: {}", savedTask.getTaskId(), assignedUser.getUsername());
        return taskMapper.toTaskDto(savedTask);
    }

    public TaskDto getTaskById(Long taskId) {
        String username = getCurrentUsername();
        log.info("Admin fetching specific task by id {} for user: {}", taskId, username);

        Task task = findTaskById(taskId);
        log.debug("Admin fetched the task : task Id {}", taskId);
        return taskMapper.toTaskDto(task);
    }

    public List<TaskDto> getTasksAssignedToUser(Long userId) {
        String username = getCurrentUsername();
        log.info("Admin {} fetching specific tasks assigned to user for user: {}", username, userId);

        AppUser appUser = userInterface.findById(userId);
        List<Task> userTasks = appUser.getTasks();

        return userTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> getTasksYouAssigned() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching tasks that they assigned to users", username);

        Admin admin = getCurrentAdmin();
        List<Task> adminTasks = admin.getTasks();

        return adminTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<AppUserDto> myUsers() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching their users", username);

        Admin admin = getCurrentAdmin();
        List<AppUser> myUsers = admin.getAppUser();

        return myUsers.stream()
                .map(appUser -> new AppUserDto(appUser.getUsername(), appUser.getEmail(), appUser.getPhone()))
                .toList();
    }

    public List<NotificationDto> adminNotifications() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching their notifications", username);

        Admin admin = getCurrentAdmin();
        List<Notification> notifications = admin.getNotifications();
        if (notifications == null) {
            return Collections.emptyList();
        }

        return notifications.stream()
                .map(notification -> new NotificationDto(
                        notification.getMessage(),
                        notification.getType(),
                        notification.getCreatedAt()))
                .toList();
    }

    public List<TaskDto> searchTasksByKeyword(List<String> keywords) {
        String username = getCurrentUsername();
        log.info("Admin {} searching tasks with keywords: {}", username, keywords);
        Admin admin = getCurrentAdmin();

        List<Task> tasks = taskRepository.findByAssignedByAndKeywordsIn(admin, keywords);
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public List<TaskDto> assignTaskToMultipleUsers(AdminMultipleTaskRequest multipleTaskRequest) {
        String username = getCurrentUsername();
        List<Long> userIds = multipleTaskRequest.assignedTo();
        log.info("Admin {} assigning task to multiple users: {}", username, userIds);

        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("At least one user ID must be provided");
        }

        Admin admin = getCurrentAdmin();
        List<AppUser> users = userIds.stream()
                .map(userInterface::findById)
                .filter(user -> user.getAdmin().getAdminId().equals(admin.getAdminId()))
                .toList();

        if (users.size() != userIds.size()) {
            throw new TaskCannotBeAssignedException("Some users are not related to this admin");
        }

        List<Task> tasks = users.stream()
                .map(user -> taskMapper.toAdminTaskEntity(multipleTaskRequest, user, admin))
                .toList();

        List<Task> savedTasks = taskRepository.saveAll(tasks);
        savedTasks.forEach(task -> notificationService.sendNotificationToAdmin(admin, NotificationType.TASK_ASSIGNED,
                "Task '" + task.getTitle() + "' (ID: " + task.getTaskId() + ") assigned to user " + task.getAssignedTo().getUsername()));
        return savedTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<UserTaskStatsDto> getUserTaskStats() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching user task stats", username);
        Admin admin = getCurrentAdmin();
        List<AppUser> users = admin.getAppUser();

        return users.stream()
                .map(user -> {
                    long todo = taskRepository.countByAssignedToAndStatus(user, TaskStatus.TODO);
                    long completed = taskRepository.countByAssignedToAndStatus(user, TaskStatus.DONE);
                    long inProgress = taskRepository.countByAssignedToAndStatus(user, TaskStatus.IN_PROGRESS);
                    long overdue = taskRepository.countByAssignedToAndDueDateBefore(user, LocalDateTime.now());
                    return new UserTaskStatsDto(user.getUsername(),todo ,completed, inProgress, overdue);
                })
                .toList();
    }

    public List<TaskDto> getTasksByDateRange(String startDate, String endDate) {
        return super.getTasksByDateRange(startDate, endDate);
    }

    @Override
    public TaskDto updateTask(Long taskId, UpdateTaskRequest updateTaskRequest) {
        Task task = findTaskById(taskId);
        Admin admin = getCurrentAdmin();
        AppUser user = task.getAssignedTo();
        TaskDto updatedTask = super.updateTask(taskId, updateTaskRequest);
        notificationService.sendNotificationToAdmin(admin, NotificationType.TASK_UPDATED,
                "Task '" + task.getTitle() + "' (ID: " + taskId + ") updated successfully");
        notificationService.sendNotificationToUser(user, NotificationType.TASK_UPDATED,
                "Task '" + task.getTitle() + "' (ID: " + taskId + ") updated by admin " + admin.getUsername());
        return updatedTask;
    }

    @Override
    public String deleteTask(Long taskId) {
        Task task = findTaskById(taskId);
        Admin admin = getCurrentAdmin();
        AppUser user = task.getAssignedTo();
        String result = super.deleteTask(taskId);
        notificationService.sendNotificationToAdmin(admin, NotificationType.TASK_DELETED,
                "Task '" + task.getTitle() + "' (ID: " + taskId + ") deleted successfully");
        notificationService.sendNotificationToUser(user, NotificationType.TASK_DELETED,
                "Task '" + task.getTitle() + "' (ID: " + taskId + ") deleted by admin " + admin.getUsername());
        return result;
    }

    @Override
    protected List<Task> fetchTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        Admin admin = getCurrentAdmin();
        return taskRepository.findByAssignedByAndDueDateBetween(admin, start, end);
    }

    @Override
    protected void checkUpdatePermission(Task task, String username) {
        Admin admin = getCurrentAdmin();
        if (!task.getAssignedBy().getAdminId().equals(admin.getAdminId())) {
            throw new AccessDeniedException("You can only update tasks you assigned");
        }
    }

    @Override
    protected void checkDeletePermission(Task task, String username) {
        Admin admin = getCurrentAdmin();
        if (!task.getAssignedBy().getAdminId().equals(admin.getAdminId())) {
            log.warn("Admin {} attempted to delete task {} which they didn't assign", username, task.getTaskId());
            throw new AccessDeniedException("You can only delete tasks you assigned");
        }
    }
}
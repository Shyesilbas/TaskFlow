package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.AppUserDto;
import com.serhat.taskFlow.dto.objects.NotificationDto;
import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.objects.UserTaskStatsDto;
import com.serhat.taskFlow.dto.requests.AdminMultipleTaskRequest;
import com.serhat.taskFlow.dto.requests.AdminTaskRequest;
import com.serhat.taskFlow.dto.requests.TaskChangeRequestDto;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.dto.responses.RespondToDateChangeRequest;
import com.serhat.taskFlow.entity.*;
import com.serhat.taskFlow.entity.enums.NotificationType;
import com.serhat.taskFlow.entity.enums.RequestStatus;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.NoPermissionException;
import com.serhat.taskFlow.exception.TaskCannotBeAssignedException;
import com.serhat.taskFlow.exception.TaskCannotBeUpdatedException;
import com.serhat.taskFlow.exception.TaskNotFoundException;
import com.serhat.taskFlow.interfaces.AdminInterface;
import com.serhat.taskFlow.interfaces.DateRangeParser;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.TaskChangeRequestRepository;
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

    private final TaskChangeRequestRepository taskChangeRequestRepository;
    public AdminTaskService(TaskRepository taskRepository, TaskMapper taskMapper, DateRangeParser dateRangeParser,
                            UserInterface userInterface, AdminInterface adminInterface, NotificationService notificationService ,
                            TaskChangeRequestRepository taskChangeRequestRepository) {
        super(taskRepository, taskMapper, dateRangeParser, userInterface, adminInterface, notificationService);
        this.taskChangeRequestRepository=taskChangeRequestRepository;
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
        notificationService.sendNotificationToUser(assignedUser, NotificationType.TASK_ASSIGNED,
                "Task '" + savedTask.getTitle() + "' (ID: " + savedTask.getTaskId() + ") assigned to user " + assignedUser.getUsername());
        log.info("Task created successfully with ID: {} for user: {}", savedTask.getTaskId(), assignedUser.getUsername());
        return taskMapper.toTaskDto(savedTask);
    }

    public TaskDto getTaskById(Long taskId) {
        String username = getCurrentUsername();
        log.info("Admin fetching specific task by id {} for user: {}", taskId, username);
        Admin admin = getCurrentAdmin();
        Task task = findTaskById(taskId);
        if(!admin.equals(task.getAssignedBy())){
            throw new NoPermissionException("You can only display your tasks!");
        }

        log.debug("Admin fetched the task : task Id {}", taskId);
        return taskMapper.toTaskDto(task);
    }

    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        String username = getCurrentUsername();
        log.info("Admin {} fetching tasks by status: {}", username, status);
        Admin admin = getCurrentAdmin();

        List<Task> tasks = taskRepository.findByAssignedByAndStatus(admin, status);
        if(tasks.isEmpty()){
            return Collections.emptyList();
        }
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> getTasksIAssignedToUser(Long userId) {
        String username = getCurrentUsername();
        log.info("Admin {} fetching tasks they assigned to user with ID: {}", username, userId);

        Admin admin = getCurrentAdmin();
        AppUser appUser = userInterface.findById(userId);

        if (appUser.getAdmin() == null || !appUser.getAdmin().getAdminId().equals(admin.getAdminId())) {
            throw new NoPermissionException("You can only view tasks that you have assigned to this user.");
        }

        List<Task> userTasks = taskRepository.findByAssignedToAndAssignedBy(appUser, admin);
        if(userTasks.isEmpty()){
            return Collections.emptyList();
        }
        return userTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public void respondToDueDateChangeRequest(RespondToDateChangeRequest respondToDateChangeRequest ) {
        String username = getCurrentUsername();
        Long requestId = respondToDateChangeRequest.requestId();
        boolean approved = respondToDateChangeRequest.approved();
        String adminMessage = respondToDateChangeRequest.adminMessage();
        log.info("Admin {} responding to due date change request {}: approved={}", username, requestId, approved);
        Admin admin = getCurrentAdmin();

        TaskChangeRequest request = taskChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new TaskNotFoundException("Change request with ID " + requestId + " not found"));

        Task task = request.getTask();
        if (!task.getAssignedBy().getAdminId().equals(admin.getAdminId())) {
            throw new AccessDeniedException("You can only respond to requests for tasks you assigned");
        }

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new TaskCannotBeUpdatedException("This request has already been processed");
        }

        if (approved) {
            task.setDueDate(request.getRequestedDueDate());
            request.setStatus(RequestStatus.APPROVED);
            notificationService.sendNotificationToUser(task.getAssignedTo(), NotificationType.TASK_CHANGE_APPROVED,
                    "Your request to change due date for task '" + task.getTitle() + "' (ID: " + task.getTaskId() + ") to " +
                            request.getRequestedDueDate() + " has been approved");
        } else {
            request.setStatus(RequestStatus.REJECTED);
            request.setAdminMessage(adminMessage);
            notificationService.sendNotificationToUser(task.getAssignedTo(), NotificationType.TASK_CHANGE_REJECTED,
                    "Your request to change due date for task '" + task.getTitle() + "' (ID: " + task.getTaskId() + ") was rejected. Reason: " + adminMessage);
        }

        taskRepository.save(task);
        taskChangeRequestRepository.save(request);

        notificationService.sendNotificationToAdmin(admin, NotificationType.TASK_CHANGE_REQUEST_RESPONDED,
                "Responded to due date change request for task '" + task.getTitle() + "' (ID: " + task.getTaskId() + ") with status: " + request.getStatus());
    }

    public List<TaskChangeRequestDto> getPendingDueDateChangeRequests() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching pending due date change requests", username);
        Admin admin = getCurrentAdmin();

        List<Task> adminTasks = taskRepository.findByAssignedBy(admin);
        List<Long> taskIds = adminTasks.stream().map(Task::getTaskId).toList();



        List<TaskChangeRequest> requests = taskChangeRequestRepository.findByTask_TaskIdInAndStatus(taskIds, RequestStatus.PENDING);
        return requests.stream()
                .map(request -> new TaskChangeRequestDto(
                        request.getId(),
                        request.getTask().getTaskId(),
                        request.getTask().getTitle(),
                        request.getRequestedDueDate(),
                        request.getStatus(),
                        request.getAdminMessage(),
                        request.getUserMessage(),
                        request.getCreatedAt(),
                        request.getUpdatedAt()))
                .toList();
    }

    public List<TaskDto> getTasksYouAssigned() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching tasks that they assigned to users", username);

        Admin admin = getCurrentAdmin();
        List<Task> adminTasks = admin.getTasks();

        if(adminTasks.isEmpty()){
            return Collections.emptyList();
        }

        return adminTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<AppUserDto> myUsers() {
        String username = getCurrentUsername();
        log.info("Admin {} fetching their users", username);

        Admin admin = getCurrentAdmin();
        List<AppUser> myUsers = admin.getAppUser();
        if(myUsers.isEmpty()){
            return Collections.emptyList();
        }

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
        if(tasks.isEmpty()){
            return Collections.emptyList();
        }
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
        if(users.isEmpty()){
            return Collections.emptyList();
        }
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
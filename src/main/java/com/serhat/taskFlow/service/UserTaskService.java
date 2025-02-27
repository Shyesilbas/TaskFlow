package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.NotificationDto;
import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.objects.TaskStatsDto;
import com.serhat.taskFlow.dto.requests.*;
import com.serhat.taskFlow.entity.*;
import com.serhat.taskFlow.entity.enums.NotificationType;
import com.serhat.taskFlow.entity.enums.TaskPriority;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.TaskCannotBeUpdatedException;
import com.serhat.taskFlow.exception.TaskNotFoundException;
import com.serhat.taskFlow.interfaces.AdminInterface;
import com.serhat.taskFlow.interfaces.DateRangeParser;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.NotificationRepository;
import com.serhat.taskFlow.repository.TaskChangeRequestRepository;
import com.serhat.taskFlow.repository.TaskRepository;
import com.serhat.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class UserTaskService extends BaseTaskService {
    private final TaskChangeRequestRepository taskChangeRequestRepository;
    private final UserRepository userRepository;

    public UserTaskService(UserRepository userRepository,TaskRepository taskRepository, TaskMapper taskMapper, DateRangeParser dateRangeParser,
                           UserInterface userInterface, AdminInterface adminInterface , NotificationService notificationService, TaskChangeRequestRepository taskChangeRequestRepository) {
        super(taskRepository, taskMapper, dateRangeParser, userInterface, adminInterface, notificationService);
        this.taskChangeRequestRepository = taskChangeRequestRepository;
        this.userRepository=userRepository;
    }

    /*
    @Transactional
    public TaskDto createTask(UserTaskRequest taskRequest) {
        String username = getCurrentUsername();
        log.info("Creating task for user: {}", username);

        AppUser currentUser = getCurrentUser();
        Task task = taskMapper.toUserTaskEntity(taskRequest, currentUser);

        Task savedTask = taskRepository.save(task);
        notificationService.sendNotificationToUser(currentUser, NotificationType.TASK_ASSIGNED,
                "Task '" + savedTask.getTitle() + "' (ID: " + savedTask.getTaskId() + ") created successfully");
        log.info("Task created successfully with ID: {} for user: {}", savedTask.getTaskId(), username);
        return taskMapper.toTaskDto(savedTask);
    }

    public List<TaskDto> tasksICreated() {
        String username = getCurrentUsername();
        log.info("Fetching tasks created by user: {}", username);

        AppUser user = getCurrentUser();
        List<Task> tasksICreated = taskRepository.findByAssignedToAndAssignedByIsNull(user);
        log.debug("User fetched {} tasks they created", tasksICreated.size());

        return tasksICreated.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }
    */

    public List<TaskDto> getTasksByPriority(TaskPriority priority) {
        String username = getCurrentUsername();
        log.info("User {} fetching tasks by priority: {}", username, priority);
        AppUser user = getCurrentUser();
        List<Task> tasks = taskRepository.findByAssignedToAndTaskPriority(user, priority);
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> getAllTasks(){
        String username = getCurrentUsername();
        log.info("User {} fetching tasks", username);
        AppUser user = getCurrentUser();
        List<Task> tasks = taskRepository.findByAssignedTo(user);
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> tasksAssignedToMe() {
        try {
            String username = getCurrentUsername();
            log.info("Fetching tasks assigned to user: {}", username);

            AppUser user = getCurrentUser();
            List<Task> tasksAssignedToMe = taskRepository.findByAssignedTo(user);
            log.debug("User fetched {} tasks assigned to them", tasksAssignedToMe.size());

            if(tasksAssignedToMe.isEmpty()){
              return Collections.emptyList();
            }

            return tasksAssignedToMe.stream()
                    .filter(task -> task.getAssignedBy() != null)
                    .map(taskMapper::toTaskDto)
                    .toList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to fetch assigned tasks: " + e.getMessage(), e);
        }
    }

    public List<TaskDto> findByStatus(TaskStatus status) {
        String username = getCurrentUsername();
        log.info("User {} listing tasks with status: {}", username, status);
        AppUser user = getCurrentUser();

        List<Task> tasksByStatus = taskRepository.findByAssignedToAndStatus(user, status);
        return tasksByStatus.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public TaskDto addCommentToTask(AddUserCommentRequest userCommentRequest) {
        String username = getCurrentUsername();
        Long taskId = userCommentRequest.taskId();
        String comment = userCommentRequest.comment();
        log.info("User {} adding comment to task {}", username, taskId);
        Task task = findTaskById(taskId);

        checkUpdatePermission(task, username);
        task.setUserComment(comment);
        Task updatedTask = taskRepository.save(task);
        log.info(username +" added the comment : "+comment + " to task with id : "+taskId);
        return taskMapper.toTaskDto(updatedTask);
    }

    public AdminDto myAdmin() {
        String username = getCurrentUsername();
        log.info("User {} looking for their admin", username);
        AppUser user = getCurrentUser();

        Admin admin = user.getAdmin();
        if (admin == null) {
            return null;
        }

        return new AdminDto(admin.getUsername(), admin.getEmail(), admin.getPhone());
    }

    @Transactional
    public void deleteNotifications(){
        String username = getCurrentUsername();
        log.info("User {} deleting notifications", username);
        AppUser user = getCurrentUser();
        user.getNotifications().clear();
        userRepository.save(user);

    }

    public TaskStatsDto getTaskStats() {
        String username = getCurrentUsername();
        log.info("User {} fetching task stats", username);
        AppUser user = getCurrentUser();

        long completed = taskRepository.countByAssignedToAndStatus(user, TaskStatus.DONE);
        long todo = taskRepository.countByAssignedToAndStatus(user, TaskStatus.TODO);
        long inProgress = taskRepository.countByAssignedToAndStatus(user, TaskStatus.IN_PROGRESS);
        long overdue = taskRepository.countByAssignedToAndDueDateBefore(user, LocalDateTime.now());
        return new TaskStatsDto(todo, inProgress, completed, overdue);
    }

    public List<TaskDto> getActiveTasks() {
        String username = getCurrentUsername();
        log.info("User {} fetching active tasks", username);
        AppUser user = getCurrentUser();

        List<Task> activeTasks = taskRepository.findByAssignedToAndStatusNot(user, TaskStatus.DONE);
        return activeTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public void requestDueDateChange(UpdateDueDateRequest updateDueDateRequest) {
        String username = getCurrentUsername();
        Long taskId = updateDueDateRequest.taskId();
        String newDueDate = updateDueDateRequest.newDate();
        String message = updateDueDateRequest.message();
        log.info("User {} requesting due date change for task {} to {}", username, taskId, newDueDate);
        AppUser user = getCurrentUser();
        Task task = findTaskById(taskId);

        checkUpdatePermission(task, username);
        if (task.getStatus() == TaskStatus.DONE) {
            throw new TaskCannotBeUpdatedException("Cannot request due date change for a completed task");
        }

        LocalDateTime parsedDueDate = dateRangeParser.parseStartDate(newDueDate);

        TaskChangeRequest request = TaskChangeRequest.builder()
                .task(task)
                .user(user)
                .requestedDueDate(parsedDueDate)
                .userMessage(message)
                .build();

        taskChangeRequestRepository.save(request);

        notificationService.sendNotificationToUser(user, NotificationType.TASK_CHANGE_REQUEST,
                "Due date change requested for task '" + task.getTitle() + "' (ID: " + taskId + ") to " + newDueDate);
        if (task.getAssignedBy() != null) {
            notificationService.sendNotificationToAdmin(task.getAssignedBy(), NotificationType.TASK_CHANGE_REQUEST,
                    username + " requested due date change for task '" + task.getTitle() + "' (ID: " + taskId + ") to " + newDueDate);
        }
    }

    public List<TaskChangeRequestDto> getMyDueDateChangeRequests() {
        String username = getCurrentUsername();
        log.info("User {} fetching their due date change requests", username);
        AppUser user = getCurrentUser();

        List<TaskChangeRequest> requests = taskChangeRequestRepository.findByUser(user);
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

    public List<NotificationDto> userNotifications() {
        String username = getCurrentUsername();
        log.info("User {} fetching their notifications", username);
        AppUser user = getCurrentUser();

        List<Notification> notifications = user.getNotifications();
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

    public List<TaskDto> fetchUpcomingTasks() {
        String username = getCurrentUsername();
        log.info("Fetching upcoming tasks for user: {}", username);

        AppUser user = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveDaysLater = now.plusDays(5);

        List<Task> upcomingTasks = taskRepository.findByAssignedToAndDueDateBetween(user, now, fiveDaysLater);
        log.debug("User {} fetched {} upcoming tasks", username, upcomingTasks.size());

        return upcomingTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> searchTasksByKeyword(List<String> keywords) {
        String username = getCurrentUsername();
        log.info("User {} searching tasks with keywords: {}", username, keywords);
        AppUser user = getCurrentUser();

        List<Task> tasks = taskRepository.findByAssignedToAndKeywordsIn(user, keywords);
        return tasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public TaskDto UpdateTaskStatus(Long taskId) {
        String username = getCurrentUsername();
        log.info("User {} marking task {} as completed", username, taskId);
        AppUser user = getCurrentUser();
        try {
            Task task = findTaskById(taskId);
            log.info("Task found: {} with status: {}", task.getTitle(), task.getStatus());

            if (task.getStatus() == TaskStatus.TODO) {
                task.setStatus(TaskStatus.IN_PROGRESS);
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                task.setStatus(TaskStatus.DONE);
            } else if (task.getStatus() == TaskStatus.DONE) {
                throw new TaskCannotBeUpdatedException("Task is done. Cannot be updated further");
            }

            checkUpdatePermission(task, username);
            Task updatedTask = taskRepository.save(task);
            log.info("Task updated to status: {}", updatedTask.getStatus());

            notificationService.sendNotificationToUser(user, NotificationType.TASK_UPDATED,
                    "Task '" + task.getTitle() + "' (ID: " + taskId + ") marked as " + task.getStatus());
            if (task.getAssignedBy() != null) {
                notificationService.sendNotificationToAdmin(task.getAssignedBy(), NotificationType.TASK_UPDATED,
                        "Task '" + task.getTitle() + "' (ID: " + taskId + ") marked as " + task.getStatus() + " by " + username);
            }
            return taskMapper.toTaskDto(updatedTask);
        } catch (TaskNotFoundException e) {
            log.error("Task not found for taskId: {}", taskId);
            throw e;
        } catch (Exception e) {
            log.error("Error updating task status for taskId: {}", taskId, e);
            throw e;
        }
    }


    @Override
    public String deleteTask(Long taskId) {
        AppUser user = getCurrentUser();
        Task task = findTaskById(taskId);
        Admin admin = task.getAssignedBy();
        String result = super.deleteTask(taskId);
        notificationService.sendNotificationToUser(user, NotificationType.TASK_DELETED,
                "Task '" + task.getTitle() + "' (ID: " + taskId + ") deleted successfully");
        if (admin != null) {
            notificationService.sendNotificationToAdmin(admin, NotificationType.TASK_DELETED,
                    "Task '" + task.getTitle() + "' (ID: " + taskId + ") deleted by user " + user.getUsername());
        }
        return result;
    }

    @Override
    protected List<Task> fetchTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        AppUser user = getCurrentUser();
        return taskRepository.findByAssignedToAndDueDateBetween(user, start, end);
    }

    @Override
    protected void checkUpdatePermission(Task task, String username) {
        if (!task.getAssignedTo().getUsername().equals(username)) {
            log.warn("User {} attempted to update task {} which they don't own", username, task.getTaskId());
            throw new AccessDeniedException("You can only update your own tasks");
        }
    }

    @Override
    protected void checkDeletePermission(Task task, String username) {
        if (!task.getAssignedTo().getUsername().equals(username)) {
            log.warn("User {} attempted to delete task {} which they don't own", username, task.getTaskId());
            throw new AccessDeniedException("You can only delete your own tasks");
        }
    }
}
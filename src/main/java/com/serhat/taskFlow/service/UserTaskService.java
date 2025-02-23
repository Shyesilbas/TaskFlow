package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.AdminDto;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.dto.requests.UserTaskRequest;
import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.interfaces.DateRangeParser;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserTaskService extends BaseTaskService {

    private final TaskMapper taskMapper;
    public UserTaskService(TaskRepository taskRepository, TaskMapper taskMapper, DateRangeParser dateRangeParser, UserInterface userInterface) {
        super(taskRepository, dateRangeParser, userInterface, taskMapper);
        this.taskMapper=taskMapper;
    }

    @Transactional
    public TaskDto createTask(UserTaskRequest taskRequest) {
        String username = getCurrentUsername();
        log.info("Creating task for user: {}", username);

        AppUser currentUser = getCurrentUser();

        Task task = taskMapper.toUserTaskEntity(taskRequest, currentUser);

        Task savedTask = taskRepository.save(task);
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

    public List<TaskDto> tasksAssignedToMe() {
        try {
            String username = getCurrentUsername();
            log.info("Fetching tasks assigned to user: {}", username);

            AppUser user = getCurrentUser();

            List<Task> tasksAssignedToMe = taskRepository.findByAssignedTo(user);
            log.debug("User fetched {} tasks assigned to them", tasksAssignedToMe.size());

            return tasksAssignedToMe.stream()
                    .filter(task -> task.getAssignedBy() != null)
                    .map(taskMapper::toTaskDto)
                    .toList();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TaskDto> findByStatus(TaskStatus status){
        String username = getCurrentUsername();
        log.info("User {} listing tasks with status: {}", username, status);
        AppUser user = getCurrentUser();

        List<Task> tasksByStatus = taskRepository.findByAssignedToAndStatus(user,status);

        return tasksByStatus.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public AdminDto myAdmin(){
        String username = getCurrentUsername();
        log.info("User {} looking for their admin", username);
        AppUser user = getCurrentUser();

        Admin admin = user.getAdmin();

        return new AdminDto(
                admin.getUsername(),
                admin.getEmail(),
                admin.getPhone()
        );
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


    @Override
    public TaskDto updateTask(Long taskId, UpdateTaskRequest updateTaskRequest) {
        return super.updateTask(taskId, updateTaskRequest);
    }

    @Override
    public String deleteTask(Long taskId) {
        return super.deleteTask(taskId);
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
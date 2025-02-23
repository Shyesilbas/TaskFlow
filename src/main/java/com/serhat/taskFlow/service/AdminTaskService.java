package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.AdminTaskRequest;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
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
import java.util.List;

@Service
@Slf4j
public class AdminTaskService extends BaseTaskService {

    private final AdminInterface adminInterface;
    private final TaskMapper taskMapper;

    public AdminTaskService(TaskRepository taskRepository,
                            TaskMapper taskMapper,
                            DateRangeParser dateRangeParser, UserInterface userInterface, AdminInterface adminInterface) {
        super(taskRepository, dateRangeParser, userInterface, taskMapper);
        this.adminInterface = adminInterface;
        this.taskMapper=taskMapper;
    }

    @Transactional
    public TaskDto createTask(AdminTaskRequest adminTaskRequest) {
        String username = getCurrentUsername();
        log.info("Admin creating task for user: {}", username);

        Admin currentAdmin = adminInterface.findByUsername(username);
        AppUser assignedUser = userInterface.findById(adminTaskRequest.assignedTo());

        if(!assignedUser.getAdmin().getAdminId().equals(currentAdmin.getAdminId())){
            throw new TaskCannotBeAssignedException("");
        }

        Task task = taskMapper.toAdminTaskEntity(adminTaskRequest, assignedUser, currentAdmin);

        log.info("Admin {} assigned task to user ID: {}", currentAdmin.getUsername(), adminTaskRequest.assignedTo());

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {} for user: {}", savedTask.getTaskId(), task.getAssignedTo().getUsername());
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

        Admin admin = adminInterface.findByUsername(username);

        List<Task> adminTasks = admin.getTasks();

        return adminTasks.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    public List<TaskDto> getTasksByDateRange(String startDate, String endDate) {
        return super.getTasksByDateRange(startDate, endDate);
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskRequest updateTaskRequest) {
        return super.updateTask(taskId, updateTaskRequest);
    }

    @Transactional
    public String deleteTask(Long taskId) {
        String username = getCurrentUsername();

        Task task = findTaskById(taskId);
        log.info("Admin deleting task with ID: {} by user: {}", taskId, username);

        taskRepository.delete(task);
        log.info("Task with ID: {} deleted successfully by admin", taskId);
        return "Task with id : " + taskId + " deleted successfully";
    }

    @Override
    protected List<Task> fetchTasksByDateRange(LocalDateTime start, LocalDateTime end) {
        Admin admin = adminInterface.findByUsername(getCurrentUsername());
        return taskRepository.findByAssignedByAndDueDateBetween(admin, start, end);
    }

    @Override
    protected void checkUpdatePermission(Task task, String username) {
        Admin admin = adminInterface.findByUsername(username);
        if (!task.getAssignedBy().getAdminId().equals(admin.getAdminId())) {
            throw new AccessDeniedException("You can only update tasks you assigned");
        }
    }

    @Override
    protected void checkDeletePermission(Task task, String username) {
        Admin admin = adminInterface.findByUsername(username);
        if (!task.getAssignedBy().getAdminId().equals(admin.getAdminId())) {
            log.warn("Admin {} attempted to delete task {} which they don't assigned", username, task.getTaskId());
            throw new AccessDeniedException("You can only delete your own tasks");
        }
    }
}
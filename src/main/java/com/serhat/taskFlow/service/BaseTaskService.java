package com.serhat.taskFlow.service;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.TaskNotFoundException;
import com.serhat.taskFlow.interfaces.DateRangeParser;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.mapper.TaskMapper;
import com.serhat.taskFlow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseTaskService {

    protected final TaskRepository taskRepository;
    protected final DateRangeParser dateRangeParser;
    protected final UserInterface userInterface;
    private final TaskMapper taskMapper;

    protected String getCurrentUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    protected Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    protected AppUser getCurrentUser() {
        String username = getCurrentUsername();
        return userInterface.findByUsername(username);
    }

    public List<TaskDto> getTasksByDateRange(String startDate, String endDate) {
        String username = getCurrentUsername();
        log.info("User {} fetching tasks by date range: startDate={}, endDate={}", username, startDate, endDate);

        LocalDateTime start = dateRangeParser.parseStartDate(startDate).toLocalDate().atStartOfDay();
        LocalDateTime end = dateRangeParser.parseEndDate(endDate).toLocalDate().atTime(23, 59);

        List<Task> tasksInRange = fetchTasksByDateRange(start, end);

        return tasksInRange.stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @Transactional
    public TaskDto updateTask(Long taskId, UpdateTaskRequest updateTaskRequest) {
        String username = getCurrentUsername();
        Task existingTask = findTaskById(taskId);
        log.info("Updating task with ID: {} by user: {}", taskId, username);

        checkUpdatePermission(existingTask, username);

        existingTask.setTitle(updateTaskRequest.title());
        existingTask.setDescription(updateTaskRequest.description());
        existingTask.setStatus(TaskStatus.valueOf(updateTaskRequest.status()));
        existingTask.setDueDate(updateTaskRequest.dueDate() != null ?
                dateRangeParser.parseStartDate(updateTaskRequest.dueDate()) : null);

        Task updated = taskRepository.save(existingTask);
        log.info("Task with ID: {} updated successfully by user: {}", taskId, username);

        return taskMapper.toTaskDto(updated);
    }

    @Transactional
    public String deleteTask(Long taskId) {
        String username = getCurrentUsername();
        Task task = findTaskById(taskId);
        log.info("Deleting task with ID: {} by user: {}", taskId, username);

        checkDeletePermission(task, username);

        taskRepository.delete(task);
        log.info("Task with ID: {} deleted successfully by user: {}", taskId, username);
        return "Task with ID: " + taskId + " deleted successfully";
    }


    protected abstract void checkUpdatePermission(Task task, String username);

    protected abstract void checkDeletePermission(Task task, String username);

    protected abstract List<Task> fetchTasksByDateRange(LocalDateTime start, LocalDateTime end);
}
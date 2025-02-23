package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.objects.NotificationDto;
import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.AdminDto;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.dto.requests.UserTaskRequest;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/user")
@RequiredArgsConstructor
public class UserTaskController {

    private final UserTaskService userTaskService;

    @PostMapping("/createYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> createUserTask(@RequestBody UserTaskRequest userTaskRequest) {
        TaskDto createdTask = userTaskService.createTask(userTaskRequest);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/myAdmin")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdminDto> myAdmin(){
        return ResponseEntity.ok(userTaskService.myAdmin());
    }

    @GetMapping("/ICreated")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksICreated() {
        List<TaskDto> tasks = userTaskService.tasksICreated();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/myNotifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        List<NotificationDto> notifications = userTaskService.userNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/assignedToMe")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksAssignedToMe() {
        List<TaskDto> tasks = userTaskService.tasksAssignedToMe();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/upcomingTasks")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getUpcomingTasks() {
        List<TaskDto> tasks = userTaskService.fetchUpcomingTasks();
        if (tasks.isEmpty()){
            return ResponseEntity.ok("No upcoming tasks found. (In five days)");
        }
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/updateYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> updateUserTask(
            @RequestParam("taskId") Long taskId,
            @RequestBody UpdateTaskRequest updateTaskRequest
    ) {
        TaskDto task = userTaskService.updateTask(taskId, updateTaskRequest);
        return ResponseEntity.ok(task);
    }
    @GetMapping("/by-date-range")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        List<TaskDto> tasks = userTaskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }
    @DeleteMapping("/deleteYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> deleteUserTask(@RequestParam("taskId") Long taskId) {
       return ResponseEntity.ok(userTaskService.deleteTask(taskId));
    }

    @GetMapping("/by-status")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@RequestParam TaskStatus taskStatus){
        return ResponseEntity.ok(userTaskService.findByStatus(taskStatus));
    }
}
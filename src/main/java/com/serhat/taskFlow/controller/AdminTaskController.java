package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.objects.AppUserDto;
import com.serhat.taskFlow.dto.objects.NotificationDto;
import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.objects.UserTaskStatsDto;
import com.serhat.taskFlow.dto.requests.AdminMultipleTaskRequest;
import com.serhat.taskFlow.dto.requests.AdminTaskRequest;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.service.AdminTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/admin")
@RequiredArgsConstructor
public class AdminTaskController {

    private final AdminTaskService adminTaskService;
    @PostMapping("/createTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> createAdminTask(@RequestBody AdminTaskRequest adminTaskRequest) {
        TaskDto createdTask = adminTaskService.createTask(adminTaskRequest);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/getTaskById")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> getTaskById(@RequestParam Long taskId) {
        TaskDto task = adminTaskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/myUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppUserDto>> getMyUsers() {
        List<AppUserDto> appUserDto = adminTaskService.myUsers();
        return ResponseEntity.ok(appUserDto);
    }

    @GetMapping("/myNotifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        List<NotificationDto> notifications = adminTaskService.adminNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/userTaskStats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserTaskStatsDto>> userTaskStats(){
        List<UserTaskStatsDto> userTaskStats = adminTaskService.getUserTaskStats();
        return ResponseEntity.ok(userTaskStats);
    }


    @GetMapping("/getTasksAssignedToUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getTasksAssignedToUser(@RequestParam Long userId) {
        List<TaskDto> task = adminTaskService.getTasksAssignedToUser(userId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasksYouAssigned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getTasksYouAssigned() {
        List<TaskDto> task = adminTaskService.getTasksYouAssigned();
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/deleteTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAdminTask(@RequestParam("taskId") Long taskId) {
        return ResponseEntity.ok(adminTaskService.deleteTask(taskId));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<TaskDto>> getTasksByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        List<TaskDto> tasks = adminTaskService.getTasksByDateRange(startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/updateTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> updateAdminTask(
            @RequestParam("taskId") Long taskId,
            @RequestBody UpdateTaskRequest updatedTaskRequest
    ) {
        TaskDto task = adminTaskService.updateTask(taskId, updatedTaskRequest);
        return ResponseEntity.ok(task);
    }
    @GetMapping("/searchTasksByKeyword")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> searchTasksByKeyword(@RequestParam List<String> keywords) {
        List<TaskDto> tasks = adminTaskService.searchTasksByKeyword(keywords);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/assignTaskToMultipleUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> assignTaskToMultipleUsers(
            @RequestBody AdminMultipleTaskRequest taskRequest) {
        List<TaskDto> tasks = adminTaskService.assignTaskToMultipleUsers(taskRequest);
        return ResponseEntity.ok(tasks);
    }

}

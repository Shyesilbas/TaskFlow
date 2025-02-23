package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.objects.TaskDto;
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


}

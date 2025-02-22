package com.serhat.jwt.controller;

import com.serhat.jwt.dto.requests.AdminTaskRequest;
import com.serhat.jwt.dto.objects.TaskDto;
import com.serhat.jwt.dto.requests.UpdateTaskRequest;
import com.serhat.jwt.dto.requests.UserTaskRequest;
import com.serhat.jwt.service.AdminTaskService;
import com.serhat.jwt.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final AdminTaskService adminTaskService;
    private final UserTaskService userTaskService;

    @PostMapping("/admin/createTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> createAdminTask(@RequestBody AdminTaskRequest adminTaskRequest) {
        TaskDto createdTask = adminTaskService.createTask(adminTaskRequest);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/admin/getTaskById")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> getTaskById(@RequestParam Long taskId) {
        TaskDto task = adminTaskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/admin/getTasksAssignedToUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getTasksAssignedToUser(@RequestParam Long userId) {
        List<TaskDto> task = adminTaskService.getTasksAssignedToUser(userId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/admin/tasksYouAssigned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getTasksYouAssigned() {
        List<TaskDto> task = adminTaskService.getTasksYouAssigned();
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/admin/deleteTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAdminTask(@RequestParam("taskId") Long taskId) {
        return ResponseEntity.ok(adminTaskService.deleteTask(taskId));
    }
    @PutMapping("/admin/updateTask")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskDto> updateAdminTask(
            @RequestParam("taskId") Long taskId,
            @RequestBody UpdateTaskRequest updatedTaskRequest
    ) {
        TaskDto task = adminTaskService.updateTask(taskId, updatedTaskRequest);
        return ResponseEntity.ok(task);
    }
    @PostMapping("/user/createYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> createUserTask(@RequestBody UserTaskRequest userTaskRequest) {
        TaskDto createdTask = userTaskService.createTask(userTaskRequest);
        return ResponseEntity.ok(createdTask);
    }
    @GetMapping("/user/ICreated")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksICreated() {
        List<TaskDto> tasks = userTaskService.tasksICreated();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/assignedToMe")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksAssignedToMe() {
        List<TaskDto> tasks = userTaskService.tasksAssignedToMe();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/user/updateYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> updateUserTask(
            @RequestParam("taskId") Long taskId,
            @RequestBody UpdateTaskRequest updateTaskRequest
    ) {
        TaskDto task = userTaskService.updateTask(taskId, updateTaskRequest);
        return ResponseEntity.ok(task);
    }



    @DeleteMapping("/user/deleteYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> deleteUserTask(@RequestParam("taskId") Long taskId) {
       return ResponseEntity.ok(userTaskService.deleteTask(taskId));

    }
}
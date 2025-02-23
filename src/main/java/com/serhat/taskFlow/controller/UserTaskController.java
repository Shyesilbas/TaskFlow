package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.requests.UpdateTaskRequest;
import com.serhat.taskFlow.dto.requests.UserTaskRequest;
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
    @GetMapping("/ICreated")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksICreated() {
        List<TaskDto> tasks = userTaskService.tasksICreated();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignedToMe")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksAssignedToMe() {
        List<TaskDto> tasks = userTaskService.tasksAssignedToMe();
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
}
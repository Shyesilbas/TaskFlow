package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.objects.NotificationDto;
import com.serhat.taskFlow.dto.objects.TaskDto;
import com.serhat.taskFlow.dto.objects.TaskStatsDto;
import com.serhat.taskFlow.dto.requests.*;
import com.serhat.taskFlow.entity.enums.TaskPriority;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import com.serhat.taskFlow.exception.TaskCannotBeUpdatedException;
import com.serhat.taskFlow.exception.TaskNotFoundException;
import com.serhat.taskFlow.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/user")
@RequiredArgsConstructor
public class UserTaskController {

    private final UserTaskService userTaskService;

    /*
    @PostMapping("/createYourTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> createUserTask(@RequestBody UserTaskRequest userTaskRequest) {
        TaskDto createdTask = userTaskService.createTask(userTaskRequest);
        return ResponseEntity.ok(createdTask);
    }


     */

    @GetMapping("/myAdmin")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdminDto> myAdmin(){
        return ResponseEntity.ok(userTaskService.myAdmin());
    }


    @GetMapping("/searchTask/by-keyword")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> searchByKeyword(@RequestParam List<String> keyword){
        return ResponseEntity.ok(userTaskService.searchTasksByKeyword(keyword));
    }

    /*
    @GetMapping("/ICreated")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksICreated() {
        List<TaskDto> tasks = userTaskService.tasksICreated();
        return ResponseEntity.ok(tasks);
    }
     */

    @GetMapping("/unDoneTasks")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getUndoneTasks() {
        List<TaskDto> tasks = userTaskService.getActiveTasks();
        return ResponseEntity.ok(tasks);
    }
    @GetMapping("/taskStats")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskStatsDto> getTaskStats() {
        return ResponseEntity.ok(userTaskService.getTaskStats());
    }

    @GetMapping("/myNotifications")
    @PreAuthorize("hasRole('CUSTOMER')")
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

    @PutMapping("/addCommentToTask")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> addCommentToTask(@RequestBody AddUserCommentRequest userCommentRequest){
        return ResponseEntity.ok(userTaskService.addCommentToTask(userCommentRequest));
    }

    @PutMapping("/update-taskStatus")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TaskDto> updateTaskStatus(@RequestParam Long taskId) {
            return ResponseEntity.ok(userTaskService.UpdateTaskStatus(taskId));
    }
    @GetMapping("/tasksByPriority")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@RequestParam TaskPriority taskPriority) {
        List<TaskDto> tasks = userTaskService.getTasksByPriority(taskPriority);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/request-due-date-change")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> requestDueDateChange(@RequestBody UpdateDueDateRequest updateDueDateRequest) {
        userTaskService.requestDueDateChange(updateDueDateRequest);
        return ResponseEntity.ok("Due date change request submitted successfully");
    }

    @GetMapping("/my-due-date-change-requests")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TaskChangeRequestDto>> getMyDueDateChangeRequests() {
        List<TaskChangeRequestDto> requests = userTaskService.getMyDueDateChangeRequests();
        return ResponseEntity.ok(requests);
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
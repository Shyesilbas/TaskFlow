package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.requests.AssignRequest;
import com.serhat.taskFlow.dto.responses.AssignSuccessful;
import com.serhat.taskFlow.service.AssigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assigning")
@RequiredArgsConstructor
public class AssigningController {
    private final AssigningService assigningService;

    @PostMapping("/assignAdminToUser")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AssignSuccessful> assignAdminToUser(@RequestBody AssignRequest assignRequest){
        return ResponseEntity.ok(assigningService.assignAdminToUser(assignRequest));
    }
}

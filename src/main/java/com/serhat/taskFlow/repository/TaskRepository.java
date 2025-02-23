package com.serhat.taskFlow.repository;

import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import com.serhat.taskFlow.entity.enums.TaskPriority;
import com.serhat.taskFlow.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(AppUser assignedTo);
    List<Task> findByAssignedByAndDueDateBetween(Admin admin, LocalDateTime start, LocalDateTime end);
    List<Task> findByAssignedToAndDueDateBetween(AppUser appUser, LocalDateTime start, LocalDateTime end);

    List<Task> findByAssignedToAndStatus(AppUser user, TaskStatus status);

    List<Task> findByAssignedToAndKeywordsIn(AppUser assignedTo, List<String> keywords);
    List<Task> findByAssignedByAndKeywordsIn(Admin admin, List<String> keywords);

    List<Task> findByAssignedToAndStatusNot(AppUser user, TaskStatus taskStatus);

    long countByAssignedToAndStatus(AppUser user, TaskStatus taskStatus);

    long countByAssignedToAndDueDateBefore(AppUser user, LocalDateTime now);

    List<Task> findByAssignedToAndTaskPriority(AppUser user, TaskPriority priority);

    List<Task> findByAssignedByAndStatus(Admin admin, TaskStatus status);

    List<Task> findByAssignedToAndAssignedBy(AppUser appUser, Admin admin);

    List<Task> findByAssignedBy(Admin admin);
}
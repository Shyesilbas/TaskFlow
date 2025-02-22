package com.serhat.taskFlow.repository;

import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(AppUser assignedTo);
    List<Task> findByAssignedToAndAssignedByIsNull(AppUser assignedTo);
}
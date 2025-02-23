package com.serhat.taskFlow.repository;

import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.entity.TaskChangeRequest;
import com.serhat.taskFlow.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskChangeRequestRepository extends JpaRepository<TaskChangeRequest,Long> {
    List<TaskChangeRequest> findByUser(AppUser user);
    List<TaskChangeRequest> findByTask_TaskIdAndStatus(Long taskId, RequestStatus status);

    List<TaskChangeRequest> findByTask_TaskIdInAndStatus(List<Long> taskIds, RequestStatus requestStatus);
}

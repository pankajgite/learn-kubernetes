package com.task.taskService.repository;

import com.task.taskService.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByCreatedByOrAssignedTo(String createdBy, String assignedTo, Pageable pageable);
}

package com.task.taskService.service;

import com.task.taskService.dto.CreateTaskRequest;
import com.task.taskService.dto.TaskResponse;
import com.task.taskService.dto.UpdateTaskRequest;
import com.task.taskService.dto.UpdateTaskStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest taskRequest);

    Page<TaskResponse> getAllTasks(Pageable pageable, String status, String priority);

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, UpdateTaskRequest taskRequest);

    void deleteTask(Long id);

    TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest statusRequest);
}

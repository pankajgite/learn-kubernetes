package com.task.taskService.controller;

import com.task.taskService.dto.*;
import com.task.taskService.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest taskRequest) {
        TaskResponse createdTask = taskService.createTask(taskRequest);
        return new ResponseEntity<>(ApiResponse.success(createdTask, "Task created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        Page<TaskResponse> tasks = taskService.getAllTasks(pageable, status, priority);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Tasks retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task, "Task retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest taskRequest) {
        TaskResponse updatedTask = taskService.updateTask(id, taskRequest);
        return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest statusRequest) {
        TaskResponse updatedTask = taskService.updateTaskStatus(id, statusRequest);
        return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task status updated successfully"));
    }
}

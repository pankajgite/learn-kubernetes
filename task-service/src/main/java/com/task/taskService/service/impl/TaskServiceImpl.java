package com.task.taskService.service.impl;

import com.task.taskService.client.UserServiceClient;
import com.task.taskService.dto.CreateTaskRequest;
import com.task.taskService.dto.TaskResponse;
import com.task.taskService.dto.UpdateTaskRequest;
import com.task.taskService.dto.UpdateTaskStatusRequest;
import com.task.taskService.entity.Task;
import com.task.taskService.exception.TaskNotFoundException;
import com.task.taskService.exception.UnauthorizedTaskAccessException;
import com.task.taskService.exception.UserNotFoundException;
import com.task.taskService.repository.TaskRepository;
import com.task.taskService.security.JwtUtil;
import com.task.taskService.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    @Override
    public TaskResponse createTask(CreateTaskRequest taskRequest) {
        Long assignedToId = Long.parseLong(taskRequest.getAssignedTo());
        if (assignedToId != null && !userServiceClient.userExists(assignedToId)) {
            throw new UserNotFoundException("Assigned user not found");
        }

        Task task = modelMapper.map(taskRequest, Task.class);
        task.setCreatedBy(getCurrentUserId().toString());
        task.setAssignedTo(assignedToId.toString());
        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    public Page<TaskResponse> getAllTasks(Pageable pageable, String status, String priority) {
        // This is a simplified implementation. A more robust solution would use Specifications or Querydsl.
        String userId = getCurrentUserId().toString();
        String role = getCurrentUserRole();

        Page<Task> tasks;
        if ("ADMIN".equals(role)) {
            tasks = taskRepository.findAll(pageable);
        } else {
            tasks = taskRepository.findByCreatedByOrAssignedTo(userId, userId, pageable);
        }

        return tasks.map(task -> modelMapper.map(task, TaskResponse.class));
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        String userId = getCurrentUserId().toString();
        String role = getCurrentUserRole();

        if (!"ADMIN".equals(role) && !task.getCreatedBy().equals(userId) && !task.getAssignedTo().equals(userId)) {
            throw new UnauthorizedTaskAccessException("You are not authorized to view this task");
        }

        return modelMapper.map(task, TaskResponse.class);
    }

    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        String userId = getCurrentUserId().toString();
        String role = getCurrentUserRole();

        if (!"ADMIN".equals(role) && !task.getCreatedBy().equals(userId)) {
            throw new UnauthorizedTaskAccessException("You are not authorized to update this task");
        }

        Long assignedToId = Long.parseLong(taskRequest.getAssignedTo());
        if (assignedToId != null && !userServiceClient.userExists(assignedToId)) {
            throw new UserNotFoundException("Assigned user not found");
        }

        modelMapper.map(taskRequest, task);
        task.setAssignedTo(assignedToId.toString());
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        String userId = getCurrentUserId().toString();
        String role = getCurrentUserRole();

        if (!"ADMIN".equals(role) && !task.getCreatedBy().equals(userId)) {
            throw new UnauthorizedTaskAccessException("You are not authorized to delete this task");
        }

        taskRepository.delete(task);
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest statusRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        task.setStatus(statusRequest.getStatus());
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authorizationHeader = attributes.getRequest().getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                return jwtUtil.extractUserId(token);
            }
        }
        return null; // Or throw an exception if a user must be present
    }

    private String getCurrentUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");
    }
}

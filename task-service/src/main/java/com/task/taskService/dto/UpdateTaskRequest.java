package com.task.taskService.dto;

import com.task.taskService.entity.Priority;
import com.task.taskService.entity.TaskStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTaskRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description can be at most 500 characters")
    private String description;

    private TaskStatus status;

    private Priority priority;

    private String assignedTo;
}

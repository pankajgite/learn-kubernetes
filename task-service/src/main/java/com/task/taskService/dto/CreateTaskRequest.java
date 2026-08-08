package com.task.taskService.dto;

import com.task.taskService.entity.Priority;
import com.task.taskService.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description can be at most 500 characters")
    private String description;

    @NotNull(message = "Status is mandatory")
    private TaskStatus status;

    @NotNull(message = "Priority is mandatory")
    private Priority priority;

    private String assignedTo;
}

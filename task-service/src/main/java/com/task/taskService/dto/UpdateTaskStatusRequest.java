package com.task.taskService.dto;

import com.task.taskService.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is mandatory")
    private TaskStatus status;
}

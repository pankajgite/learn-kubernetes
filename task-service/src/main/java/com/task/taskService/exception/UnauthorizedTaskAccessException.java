package com.task.taskService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedTaskAccessException extends RuntimeException {

    public UnauthorizedTaskAccessException(String message) {
        super(message);
    }
}

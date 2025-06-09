package com.ktilelis.todo.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TodoApiException {
    private UUID uuid;
    private String message;
    private HttpStatus httpStatus;
    private List<ValidationError> validationErrors;

    public TodoApiException() {}
    public TodoApiException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public TodoApiException(UUID uuid, String message, HttpStatus httpStatus) {
        this.uuid = uuid;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public TodoApiException(String message, HttpStatus httpStatus, List<ValidationError> validationErrors) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.validationErrors = validationErrors;
    }
}

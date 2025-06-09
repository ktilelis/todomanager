package com.ktilelis.todo.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class TodoApiException {
    private String message;
    private HttpStatus httpStatus;
    private List<ValidationError> validationErrors;

    public TodoApiException() {}
    public TodoApiException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public TodoApiException(String message, HttpStatus httpStatus, List<ValidationError> validationErrors) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.validationErrors = validationErrors;
    }
}

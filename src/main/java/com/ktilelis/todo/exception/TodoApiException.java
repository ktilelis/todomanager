package com.ktilelis.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class TodoApiException {
    private String message;
    private HttpStatus httpStatus;
    private List<ValidationError> validationErrors;
}

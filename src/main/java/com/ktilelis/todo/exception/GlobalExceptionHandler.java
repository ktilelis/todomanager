package com.ktilelis.todo.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<TodoApiException> handleNotFound(NoSuchElementException ex) {
        var ae = new TodoApiException(ex.getMessage(), HttpStatus.NOT_FOUND);

        logger.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ae);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TodoApiException> handleValidation(MethodArgumentNotValidException ex) {
        logger.warn(ex.getMessage(), ex);

        var validationErrors = ex.getBindingResult().getFieldErrors().stream().map(error -> new ValidationError(error.getField(), error.getDefaultMessage())).toList();
        var exception = new TodoApiException(ex.getObjectName(), HttpStatus.BAD_REQUEST, validationErrors);

        return ResponseEntity.badRequest().body(exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<TodoApiException> handleConstraintViolation(ConstraintViolationException ex) {
        var validationErrors = ex.getConstraintViolations().stream().map(error -> new ValidationError(error.getPropertyPath().toString(), error.getMessage())).toList();

        logger.warn(ex.getMessage(), ex);

        var exception = new TodoApiException(ex.getMessage(), HttpStatus.BAD_REQUEST, validationErrors);
        return ResponseEntity.badRequest().body(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TodoApiException> handleGeneric(Exception ex, Locale locale) {
        UUID uuid = UUID.randomUUID();
        var errorMessage = this.messageSource.getMessage("exception.generic_error", new Object[]{uuid.toString()}, Locale.getDefault());
        var ae = new TodoApiException(uuid, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error(uuid + " " + ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ae);
    }
}

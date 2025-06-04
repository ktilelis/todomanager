package com.ktilelis.todo.exception;

public record ValidationError(String fieldName, String errorMessage) {
}

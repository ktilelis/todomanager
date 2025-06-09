package com.ktilelis.todo.todomanagement.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record TodoRequestDto(
        @NotBlank(message = "Title must not be blank")
        @Size(max = 100, message = "Title must be at most 100 characters")
        String title,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        LocalDateTime expiresAt
) {}

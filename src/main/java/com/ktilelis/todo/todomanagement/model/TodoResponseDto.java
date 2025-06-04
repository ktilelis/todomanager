package com.ktilelis.todo.todomanagement.model;

import java.time.OffsetDateTime;

public record TodoResponseDto(
        Long id,
        String title,
        String description,
        Boolean isDone,
        OffsetDateTime expiresAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

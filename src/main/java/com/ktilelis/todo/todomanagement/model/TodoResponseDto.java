package com.ktilelis.todo.todomanagement.model;

import java.time.LocalDateTime;

public record TodoResponseDto(
        Long id,
        String title,
        String description,
        Boolean done,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

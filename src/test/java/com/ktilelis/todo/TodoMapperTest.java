package com.ktilelis.todo.todomanagement.model;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TodoMapperTest {

    private final TodoMapper mapper = Mappers.getMapper(TodoMapper.class);

    @Test
    void shouldMapEntityToDto() {
        TodoEntry entity = new TodoEntry();
        entity.setId(1L);
        entity.setTitle("Test Title");
        entity.setDescription("Test Description");
        entity.setDone(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusSeconds(3600));

        TodoResponseDto dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Test Title");
        assertThat(dto.description()).isEqualTo("Test Description");
        assertThat(dto.done()).isFalse();
        assertThat(dto.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.updatedAt()).isEqualTo(entity.getUpdatedAt());
        assertThat(dto.expiresAt()).isEqualTo(entity.getExpiresAt());
    }

    @Test
    void shouldMapDtoToEntity() {
        var expiresAt = LocalDateTime.now().plusSeconds(3600);
        TodoRequestDto dto = new TodoRequestDto("New Title", "New Desc", expiresAt);

        TodoEntry entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("New Title");
        assertThat(entity.getDescription()).isEqualTo("New Desc");
        assertThat(entity.getExpiresAt()).isEqualTo(expiresAt);
    }
}

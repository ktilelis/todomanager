package com.ktilelis.todo;

import com.ktilelis.todo.todomanagement.TodoRepository;
import com.ktilelis.todo.todomanagement.TodoService;
import com.ktilelis.todo.todomanagement.model.TodoEntry;
import com.ktilelis.todo.todomanagement.model.TodoMapper;
import com.ktilelis.todo.todomanagement.model.TodoRequestDto;
import com.ktilelis.todo.todomanagement.model.TodoResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private TodoService todoService;

    @Test
    void getTodos_shouldReturnPagedTodos() {
        var entry = new TodoEntry();
        var dto = new TodoResponseDto(1L, "title", "desc", false, null, LocalDateTime.now(), LocalDateTime.now());
        Page<TodoEntry> entityPage = new PageImpl<>(List.of(entry));

        when(todoRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(todoMapper.toDto(entry)).thenReturn(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<TodoResponseDto> result = todoService.getTodos(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("title");
    }

    @Test
    void createTodo_shouldSaveAndReturnDto() {
        var req = new TodoRequestDto("new", "desc", LocalDateTime.now());
        var entity = new TodoEntry();
        var savedEntity = new TodoEntry();
        var dto = new TodoResponseDto(1L, "new", "desc", false, null, LocalDateTime.now(), LocalDateTime.now());

        when(todoMapper.toEntity(req)).thenReturn(entity);
        when(todoRepository.save(entity)).thenReturn(savedEntity);
        when(todoMapper.toDto(savedEntity)).thenReturn(dto);

        TodoResponseDto result = todoService.createTodo(req);

        assertThat(result.title()).isEqualTo("new");
    }

    @Test
    void updateTodo_shouldUpdateAndReturnDto() {
        Long id = 1L;
        var existing = new TodoEntry();
        existing.setId(id);
        existing.setTitle("old");

        var req = new TodoRequestDto("updated", "desc", LocalDateTime.now());
        var saved = new TodoEntry();
        var dto = new TodoResponseDto(id, "updated", "desc", false, null, LocalDateTime.now(), LocalDateTime.now());

        when(todoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(todoRepository.save(existing)).thenReturn(saved);
        when(todoMapper.toDto(saved)).thenReturn(dto);

        TodoResponseDto result = todoService.updateTodo(id, req);

        assertThat(result.title()).isEqualTo("updated");
        assertThat(existing.getTitle()).isEqualTo("updated");
    }

    @Test
    void getTodoById_shouldReturnDtoIfExists() {
        Long id = 1L;
        var entity = new TodoEntry();
        var dto = new TodoResponseDto(id, "sample", "desc", false, null, LocalDateTime.now(), LocalDateTime.now());

        when(todoRepository.findById(id)).thenReturn(Optional.of(entity));
        when(todoMapper.toDto(entity)).thenReturn(dto);

        TodoResponseDto result = todoService.getTodoById(id);

        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    void getTodoById_shouldThrowIfNotFound() {
        Long id = 1L;
        when(todoRepository.findById(id)).thenReturn(Optional.empty());
        when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("Not found: " + id);

        assertThatThrownBy(() -> todoService.getTodoById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Not found");
    }

    @Test
    void deleteTodo_shouldDeleteIfExists() {
        Long id = 1L;
        var entry = new TodoEntry();

        when(todoRepository.existsById(id)).thenReturn(true);
        when(todoRepository.findById(id)).thenReturn(Optional.of(entry));

        todoService.deleteTodo(id);

        verify(todoRepository).delete(entry);
    }

    @Test
    void deleteTodo_shouldThrowIfNotFound() {
        Long id = 1L;
        when(todoRepository.existsById(id)).thenReturn(false);
        when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("Not found");

        assertThatThrownBy(() -> todoService.deleteTodo(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Not found");
    }

    @Test
    void deleteTodos_shouldCallDeleteAllById() {
        List<Long> ids = List.of(1L, 2L);
        todoService.deleteTodos(ids);

        verify(todoRepository).deleteAllById(ids);
    }
}

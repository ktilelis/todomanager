package com.ktilelis.todo.todomanagement;

import com.ktilelis.todo.todomanagement.model.TodoEntry;
import com.ktilelis.todo.todomanagement.model.TodoMapper;
import com.ktilelis.todo.todomanagement.model.TodoRequestDto;
import com.ktilelis.todo.todomanagement.model.TodoResponseDto;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoMapper mapper;
    private final MessageSource messageSource;

    public TodoService(TodoRepository todoRepository, TodoMapper mapper, MessageSource messageSource) {
        this.todoRepository = todoRepository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    @Transactional(readOnly = true)
    public Page<TodoResponseDto> getTodos(Pageable pageable) {
        return this.todoRepository.findAll(pageable).map(mapper::toDto);
    }

    public TodoResponseDto createTodo(TodoRequestDto teReq) {
        var todoEntry = this.mapper.toEntity(teReq);
        var todoEntity = this.todoRepository.save(todoEntry);
        return this.mapper.toDto(todoEntity);
    }

    public TodoResponseDto updateTodo(Long id, TodoRequestDto teReq) {
        var existing = this.getTodo(id);

        existing.setTitle(teReq.title());
        existing.setDescription(teReq.description());
        existing.setExpiresAt(teReq.expiresAt());

        var todoEntity = this.todoRepository.save(existing);
        return this.mapper.toDto(todoEntity);
    }

    @Transactional(readOnly = true)
    public TodoResponseDto getTodoById(Long id) {
        return this.mapper.toDto(this.getTodo(id));
    }

    public void deleteTodo(Long id) {
        if (!this.todoRepository.existsById(id)) {
            throw new NoSuchElementException(this.messageSource.getMessage("exception.not_found", new Object[]{id}, Locale.getDefault()));
        }
        this.todoRepository.delete(this.getTodo(id));
    }

    public void deleteTodos(List<Long> ids) {
        this.todoRepository.deleteAllById(ids);
    }

    private TodoEntry getTodo(Long id) {
        return this.todoRepository.findById(id).orElseThrow(() -> new NoSuchElementException(this.messageSource.getMessage("exception.not_found", new Object[]{id}, Locale.getDefault())));
    }
}

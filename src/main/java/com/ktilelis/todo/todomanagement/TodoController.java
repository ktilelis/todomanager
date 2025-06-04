package com.ktilelis.todo.todomanagement;

import com.ktilelis.todo.todomanagement.model.TodoRequestDto;
import com.ktilelis.todo.todomanagement.model.TodoResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/todo")
@Validated
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping()
    public Page<TodoResponseDto> getTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "updatedAt") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        var pageInfo = PageRequest.of(page, pageSize, sortDirection, sortField);
        return this.todoService.getTodos(pageInfo);
    }

    @GetMapping("/{id}")
    public TodoResponseDto getTodo(@Min(0) @PathVariable Long id) {
        return this.todoService.getTodoById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTodo(@Min(0) @PathVariable Long id, @Valid @RequestBody TodoRequestDto todo) {
        this.todoService.updateTodo(id, todo);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponseDto createTodo(@Valid @RequestBody TodoRequestDto todo) {
        return this.todoService.createTodo(todo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@Min(0) @PathVariable Long id) {
        this.todoService.deleteTodo(id);
    }
}

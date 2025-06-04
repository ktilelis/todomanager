package com.ktilelis.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktilelis.todo.todomanagement.TodoController;
import com.ktilelis.todo.todomanagement.TodoService;
import com.ktilelis.todo.todomanagement.model.TodoRequestDto;
import com.ktilelis.todo.todomanagement.model.TodoResponseDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    private static final String BASE_URL = "/v1/todo";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTodos_shouldReturnPageOfTodos() throws Exception {
        TodoResponseDto dto = new TodoResponseDto(1L, "Test", "Description", false, OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now());
        Page<TodoResponseDto> page = new PageImpl<>(List.of(dto));

        Mockito.when(this.todoService.getTodos(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getTodo_shouldReturnTodoById() throws Exception {
        TodoResponseDto dto = new TodoResponseDto(1L, "Test", "Description", false, OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(this.todoService.getTodoById(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTodo_shouldReturnCreatedTodo() throws Exception {
        TodoRequestDto requestDto = new TodoRequestDto("Test", "Description", null);
        TodoResponseDto responseDto = new TodoResponseDto(1L, "Test", "Description", false, OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now());

        Mockito.when(this.todoService.createTodo(any(TodoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateTodo_shouldReturnNoContent() throws Exception {
        TodoRequestDto requestDto = new TodoRequestDto("Updated", "Updated desc", null);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());

        Mockito.verify(todoService).updateTodo(eq(1L), any(TodoRequestDto.class));
    }

    @Test
    void deleteTodo_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1")).andExpect(status().isNoContent());

        Mockito.verify(this.todoService).deleteTodo(1L);
    }
}

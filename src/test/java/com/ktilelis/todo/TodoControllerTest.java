package com.ktilelis.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktilelis.todo.configuration.AuditingConfiguration;
import com.ktilelis.todo.todomanagement.TodoController;
import com.ktilelis.todo.todomanagement.TodoService;
import com.ktilelis.todo.todomanagement.model.TodoMapper;
import com.ktilelis.todo.todomanagement.model.TodoMapperImpl;
import com.ktilelis.todo.todomanagement.model.TodoRequestDto;
import com.ktilelis.todo.todomanagement.model.TodoResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class, excludeAutoConfiguration = AuditingConfiguration.class)
class TodoControllerTest {

    private static final String BASE_URL = "/v1/todo";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    TodoMapper todoMapper = new TodoMapperImpl();

    @Test
    @DisplayName("Should return OK when performing GET Todos")
    void getTodos_shouldReturnPageOfTodos() throws Exception {
        TodoResponseDto dto = new TodoResponseDto(1L, "Test", "Description", false, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Page<TodoResponseDto> page = new PageImpl<>(List.of(dto));

        Mockito.when(this.todoService.getTodos(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should return OK when performing GET Todo by Id")
    void getTodo_shouldReturnTodoById() throws Exception {
        var dto = new TodoResponseDto(1L, "Test", "Description", false, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(this.todoService.getTodoById(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should return NOT_FOUND when requesting non existing TODO by ID")
    void getTodo_shouldReturnNotFoundWhenRequestingNonExistingId() throws Exception {
        when(todoService.getTodoById(1L)).thenThrow(new NoSuchElementException());
        mockMvc.perform(get(BASE_URL + "/{id}", 1L)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when sending an invalid request")
    void getTodo_shouldReturnBadRequestWhenSendingInvalidRequest() throws Exception {
        var todoRequest = new TodoRequestDto(null, "description", LocalDateTime.now());
        mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return CREATED and the created Todo when creating a todo")
    void createTodo_shouldReturnCreatedTodo() throws Exception {
        TodoRequestDto requestDto = new TodoRequestDto("Test", "Description", null);
        TodoResponseDto responseDto = new TodoResponseDto(1L, "Test", "Description", false, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(this.todoService.createTodo(any(TodoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should return NO_CONTENT when updating a Todo")
    void updateTodo_shouldReturnNoContent() throws Exception {
        var requestDto = new TodoRequestDto("Updated", "Updated desc", null);
        var responseDto = new TodoResponseDto(1L, requestDto.title(), requestDto.description(), false, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(todoService.updateTodo(1L, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return NO_CONTENT when deleting a Todo")
    void deleteTodo_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1")).andExpect(status().isNoContent());

        Mockito.verify(this.todoService).deleteTodo(1L);
    }


}

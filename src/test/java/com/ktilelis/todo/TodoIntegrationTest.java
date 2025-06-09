package com.ktilelis.todo;

import com.ktilelis.todo.exception.TodoApiException;
import com.ktilelis.todo.todomanagement.model.TodoRequestDto;
import com.ktilelis.todo.todomanagement.model.TodoResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TodoIntegrationTest {

    private final static String BASE_URL = "/v1/todo";

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> DB_CONTAINER = new PostgreSQLContainer<>("postgres:17.5-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageSource messageSource;


    @Test
    @DisplayName("PostgreSQL container is up and running")
    public void shouldSpinUpAPostgresqlContainer() {
        assertThat(DB_CONTAINER.isRunning()).isTrue();
    }

    @Test
    @DisplayName("should fetch all available Todo entries")
    @Sql(statements = {
            "INSERT INTO TODO_ENTRIES(title, description, is_done) VALUES ('title', 'description', false)",
            "INSERT INTO TODO_ENTRIES(title, description, is_done) VALUES ('title 2', 'description 2', false)"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM TODO_ENTRIES", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldFetchAllTodos() {
        final var response = this.restTemplate.exchange(BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<TestCustomPage<TodoResponseDto>>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().page().totalPages()).isEqualTo(1);
        assertThat(response.getBody().page().totalElements()).isEqualTo(2);
        assertThat(response.getBody().content().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("should fetch a Todo given an ID")
    @Sql(statements =
            "INSERT INTO TODO_ENTRIES(title, description, is_done) VALUES ('title', 'description', false)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM TODO_ENTRIES", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnATodoGivenAnID() {
        var response = restTemplate.getForEntity(BASE_URL + "/1", TodoResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1);
        assertThat(response.getBody().title()).isEqualTo("title");
        assertThat(response.getBody().description()).isEqualTo("description");
    }

    @Test
    @DisplayName("should return NOT_FOUND when requesting a Todo with non-existent ID")
    public void shouldReturnNotFoundWhenRequestingNonExistentID() {
        final var response = restTemplate.exchange(BASE_URL + "/999", HttpMethod.GET, null, TodoApiException.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        final var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        var expectedErrorMessage = messageSource.getMessage("exception.not_found", new Object[]{999}, Locale.getDefault());
        assertThat(body.getMessage()).isEqualTo(expectedErrorMessage);
    }

    @Test
    @DisplayName("should return BAD_REQUEST when requesting a todo with a negative ID")
    public void shouldReturnBadRequestWhenRequestingNegativeID() {
        final var response = restTemplate.getForEntity(BASE_URL + "/-1", TodoApiException.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        final var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getMessage()).isEqualTo("getTodo.id: must be greater than or equal to 0");
        assertThat(body.getValidationErrors().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("should return BAD_REQUEST when creating a new Todo with invalid properties")
    @Sql(statements = "DELETE FROM TODO_ENTRIES", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnBadRequestWhenCreatingInvalidQuote() {
        final var quote = new TodoRequestDto(null, null, null);
        final var entity = new HttpEntity<>(quote);
        final var response = restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, TodoApiException.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        final var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getMessage()).isEqualTo("todoRequestDto");
        assertThat(body.getValidationErrors().size()).isEqualTo(1);
        assertThat(body.getValidationErrors().get(0).fieldName()).isEqualTo("title");
        assertThat(body.getValidationErrors().get(0).errorMessage()).isEqualTo("Title must not be blank");
    }

    @Test
    @DisplayName("should return CREATED when creating a new todo")
    @Sql(statements = "DELETE FROM todo_entries", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnNoContentWhenCreatingTodo() {
        final var todo = new TodoRequestDto("natalia", "natalia quote", null);
        final var postResponse = restTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(todo), Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("should return NO_CONTENT when updating a todo")
    @Sql(statements = "INSERT INTO todo_entries(title, description, is_done) VALUES ('natalia', 'natalia quote', false)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM todo_entries", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnNoContentWhenUpdatingAQuote() {

        var pagedResults = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<TestCustomPage<TodoResponseDto>>() {
        });
        assertThat(pagedResults.getStatusCode()).isEqualTo(HttpStatus.OK);

        var responseBody = pagedResults.getBody();
        assertThat(responseBody.page().totalElements()).isEqualTo(1);
        var todoId = responseBody.content().get(0).id();
        final var todoForUpdate = new TodoRequestDto("natalia", "natalia quote updated", null);

        final var entity = new HttpEntity<>(todoForUpdate);
        final var response = restTemplate.exchange(BASE_URL + "/" + todoId.toString(), HttpMethod.PUT, entity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        final var updatedQuoteResponse = restTemplate.getForEntity(BASE_URL + "/" + todoId, TodoResponseDto.class);
        final var updatedQuote = updatedQuoteResponse.getBody();
        assertThat(updatedQuote).isNotNull();
        assertThat(updatedQuote.id()).isEqualTo(todoId);
        assertThat(updatedQuote.title()).isEqualTo(todoForUpdate.title());
        assertThat(updatedQuote.description()).isEqualTo(todoForUpdate.description());
    }

    @Test
    @DisplayName("should return NO_CONTENT when deleting a todo")
    @Sql(statements = "INSERT INTO todo_entries(title, description, is_done) VALUES ('natalia', 'natalia quote', false)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM todo_entries", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldReturnNoContentWhenDeletingAQuote() {
        var pagedResults = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<TestCustomPage<TodoResponseDto>>() {
        });
        assertThat(pagedResults.getStatusCode()).isEqualTo(HttpStatus.OK);

        var responseBody = pagedResults.getBody();
        assertThat(responseBody.page().totalElements()).isEqualTo(1);
        var todoId = responseBody.content().get(0).id();
        final var response = restTemplate.exchange(BASE_URL + "/" + todoId, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}

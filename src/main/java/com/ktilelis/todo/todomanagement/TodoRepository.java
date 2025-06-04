package com.ktilelis.todo.todomanagement;

import com.ktilelis.todo.todomanagement.model.TodoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntry, Long> {
}

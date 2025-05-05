package com.lloyds.consumerlending.todo.repository;

import com.lloyds.consumerlending.todo.domain.Todo;
import java.util.List;

public interface TodoRepository {
    Todo save(Todo todo);
    Todo findById(Long id);
    List<Todo> findAll();
    void delete(Long id);
    void deleteAll();
}
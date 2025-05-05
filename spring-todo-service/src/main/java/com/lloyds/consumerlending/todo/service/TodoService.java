package com.lloyds.consumerlending.todo.service;

import com.lloyds.consumerlending.todo.domain.Todo;
import java.util.List;

public interface TodoService {
    Todo create(Todo todo);
    Todo getById(Long id);
    List<Todo> listAll();
    Todo update(Todo todo);
    void delete(Long id);
}
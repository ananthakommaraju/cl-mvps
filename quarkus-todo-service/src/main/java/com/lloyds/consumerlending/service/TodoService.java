package com.lloyds.consumerlending.service;

import com.lloyds.consumerlending.domain.Todo;
import java.util.List;
import java.util.Optional;

public interface TodoService {
    Todo create(Todo todo);
    Optional<Todo> getById(Long id);
    List<Todo> listAll();
    Todo update(Todo todo);
    void delete(Long id);
}
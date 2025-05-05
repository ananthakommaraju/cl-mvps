package com.lloyds.consumerlending.repository;

import com.lloyds.consumerlending.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    List<Todo> findAll();
    void delete(Long id);
    void deleteAll();
}
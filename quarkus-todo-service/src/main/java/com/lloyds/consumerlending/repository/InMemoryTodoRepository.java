package com.lloyds.consumerlending.repository;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.lloyds.consumerlending.domain.Todo;

@ApplicationScoped
public class InMemoryTodoRepository implements TodoRepository {

    private Long nextId = 1L;
    private final Map<Long, Todo> map = new ConcurrentHashMap<>();

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(nextId++);
        }
        map.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<Todo> findAll() {
        return map.values().stream().toList();
    }

    @Override
    public void delete(Long id) {
        map.remove(id);
    }

    @Override
    public void deleteAll() {
        map.clear();
    }
}
package com.lloyds.consumerlending.todo.repository;

import com.lloyds.consumerlending.todo.domain.Todo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
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
        return new ArrayList<>(map.values());
    }

    @Override
    public void delete(Todo todo) {
        map.remove(todo.getId());
    }

    @Override
    public void deleteAll() {
        map.clear();
    }
}
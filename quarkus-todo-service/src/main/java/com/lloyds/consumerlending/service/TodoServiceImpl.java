package com.lloyds.consumerlending.service;

import com.lloyds.consumerlending.repository.TodoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

import com.lloyds.consumerlending.domain.Todo;


@ApplicationScoped
public class TodoServiceImpl implements TodoService {

    @Inject
    TodoRepository todoRepository;


    public Todo create(Todo todo) {
        return todoRepository.save(todo);
    }


    public Todo getById(Long id) {
        return todoRepository.findById(id)
        .orElse(null);
    }


    public List<Todo> listAll() {
        return todoRepository.findAll();
    }


    public Todo update(Todo todo) {
        return todoRepository.findById(todo.getId()) != null ? todoRepository.save(todo) : null;
    }


    public void delete(Long id) {
        todoRepository.delete(id);
    }
}
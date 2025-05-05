package com.lloyds.consumerlending.todo.service;

import com.lloyds.consumerlending.todo.domain.Todo;
import com.lloyds.consumerlending.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Todo create(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public Todo getById(Long id) {
        return todoRepository.findById(id);
    }

    @Override
    public List<Todo> listAll() {
        return todoRepository.findAll();
    }

    @Override
    public Todo update(Todo todo) {
        Todo existingTodo = todoRepository.findById(todo.getId());
        if (existingTodo == null) {
            return null;
        }
        return todoRepository.save(todo);
    }

    @Override
    public void delete(Long id) {
        todoRepository.delete(id);
    }
}
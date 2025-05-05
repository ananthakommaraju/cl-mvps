package com.lloyds.consumerlending.todo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Todo {
    private Long id;
    private String description;
    private boolean completed;
}
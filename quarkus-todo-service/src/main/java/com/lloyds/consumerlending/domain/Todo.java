package com.lloyds.consumerlending.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Todo {
    private Long id;
    private String description;
    private boolean completed;
}
package com.lloyds.consumerlending.controller;

import com.lloyds.consumerlending.domain.Todo;
import com.lloyds.consumerlending.service.TodoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/todo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoController {

    @Inject
    TodoService todoService;

    @POST
    public Response create(Todo todo) {
        Todo createdTodo = todoService.create(todo);
        return Response.ok(createdTodo).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Todo todo = todoService.getById(id);
        if (todo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(todo).build();
    }

    @GET
    public Response listAll() {
        List<Todo> todos = todoService.listAll();
        return Response.ok(todos).build();
    }

    @PUT
    public Response update(Todo todo) {
        Todo updatedTodo = todoService.update(todo);
        if (updatedTodo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updatedTodo).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        todoService.delete(id);
        return Response.ok().build();
    }
}
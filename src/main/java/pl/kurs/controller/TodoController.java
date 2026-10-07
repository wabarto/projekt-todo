package pl.kurs.controller;

import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;
import pl.kurs.service.TodoService;

import java.util.List;

public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // POST /todos - 201 Created -> TodoResponse
    public TodoResponse handleCreate(CreateTodoRequest request) {
        return todoService.create(request);
    }


    // GET /todos/{id} -> 200 OK albo 404 jak po
    public TodoResponse handleGetById(Long id) {
        return todoService.findById(id);
    }

    // GET /todos?completed=false -> 200 OK
    public List<TodoResponse> handleGetAll(Boolean completed) {
        return todoService.findAll(completed);
    }


    // DELETE /todos/{id} -> 204 No Content
    public void handleDelete(Long id) {
        todoService.delete(id);
    }

}

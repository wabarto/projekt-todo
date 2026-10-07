package pl.kurs.service;

import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;

import java.util.List;

public interface TodoService {
    TodoResponse create(CreateTodoRequest request);

    TodoResponse findById(Long id);

    List<TodoResponse> findAll(Boolean completed);

    void delete(Long id);


}
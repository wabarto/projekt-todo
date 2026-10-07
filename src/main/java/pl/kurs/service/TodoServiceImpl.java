package pl.kurs.service;

import pl.kurs.domain.Todo;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;
import pl.kurs.exception.TodoNotFoundException;
import pl.kurs.exception.ValidationException;
import pl.kurs.repository.TodoRepository;

import java.util.List;

public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse create(CreateTodoRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new ValidationException("Title cannot be blank");
        }

        Todo todo = new Todo(null, request.title(), request.description());
        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    @Override
    public TodoResponse findById(Long id) {
        return todoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @Override
    public List<TodoResponse> findAll(Boolean completed) {
        List<Todo> todos = (completed == null)
                ? todoRepository.findAll()
                : todoRepository.findByCompleted(completed);

        return todos.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }

        todoRepository.deleteById(id);
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted(), todo.getCreatedAt(), todo.getCompletedAt());
    }
}

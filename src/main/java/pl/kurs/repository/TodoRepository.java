package pl.kurs.repository;

import pl.kurs.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(Long id);
    List<Todo> findAll();
    List<Todo> findByCompleted(boolean completed);
    void deleteById(Long id);
    boolean existsById(Long id);
}

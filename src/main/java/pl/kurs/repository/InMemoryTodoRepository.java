package pl.kurs.repository;

import pl.kurs.domain.Todo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTodoRepository implements TodoRepository {

    private final Map<Long, Todo> store= new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Todo save(Todo todo) {
       if (todo.getId() == null) {
           todo.assignId(sequence.incrementAndGet());
       }
       store.put(todo.getId(), todo);
       return todo;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Todo> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Todo> findByCompleted(boolean completed) {
        return store.values().stream()
                .filter(todo -> todo.isCompleted() == completed)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
}

package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.domain.Todo;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;
import pl.kurs.exception.TodoNotFoundException;
import pl.kurs.repository.TodoRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceImplMockTest {

    @Mock
    TodoRepository todoRepository;

    @InjectMocks
    TodoServiceImpl todoService;


    @Test
    void shouldReturnDtoWhenTodoExists() {
        // given
        Todo todo = new Todo(5L, "test", "test");
        when(todoRepository.findById(5L)).thenReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.findById(5L);

        // then
        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.title()).isEqualTo("test");
    }

    @Test
    void shouldThrowWhenRepositoryReturnsEmpty() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(999L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldSaveExactlyOnce() {
        CreateTodoRequest request = new CreateTodoRequest("test", "test");
        when(todoRepository.save(any(Todo.class)))
                .thenReturn(new Todo(1L, "test", "test"));

        TodoResponse response = todoService.create(request);

        assertThat(response.id()).isEqualTo(1L);

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldNotDeleteWhenTodoIsMissing() {
        when(todoRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> todoService.delete(99L))
                .isInstanceOf(TodoNotFoundException.class);

        verify(todoRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(todoRepository);

    }


    // when(repo.find(anyLong(), "PL")) -> nie skompiluje sie sensownie, test bedzie sie wywalal
    // when(repo.find(anyLong(), eq("PL")))

    @Test
    void shouldSave() {
        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        when(todoRepository.save(any(Todo.class))).thenReturn(new Todo(1L, "test", "test"));

        todoService.create(new CreateTodoRequest("test", "test"));

        verify(todoRepository).save(captor.capture());

        assertThat(captor.getValue().getTitle()).isEqualTo("test");
    }


    @Test
    void shouldPropagateRepositoryFailure() {
        when(todoRepository.save(any())).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> todoService.create(new CreateTodoRequest("test", "test")))
                .isInstanceOf(RuntimeException.class);
    }
}

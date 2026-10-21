package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.domain.Todo;
import pl.kurs.repository.InMemoryTodoRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class SpyDemoTest {

    @Spy
    InMemoryTodoRepository repository = new InMemoryTodoRepository();

    @Test
    void shouldCallRealMethodsByDefault() {
        Todo saved = repository.save(new Todo(null, "test", "test"));

        doReturn(Optional.empty()).when(repository).findById(42L);

        assertThat(repository.findById(42L)).isEmpty();
    }
}

package pl.kurs.service;

import org.junit.jupiter.api.*;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;
import pl.kurs.repository.InMemoryTodoRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoServiceImplTest {

    private TodoService todoService;

    @BeforeAll // raz przed wszystkimi testami
    static void setUpClass() {
        System.out.println("start class");
    }

    @AfterAll // raz po wszystkich testach
    static void tearDownClass() {
        System.out.println("end of class");
    }

    @BeforeEach // przed kazdym testem - świeży stan
    void setUp() {
        todoService = new TodoServiceImpl(new InMemoryTodoRepository());
    }

    @AfterEach // po kazdym tescie - sprzatania po testach
    void tearDown() {
        // czyszczenie bazy, zamkniecie pliku, polaczenia itd
    }


    @Test
    @DisplayName("create todo - add identifier and save title")
    void shouldCreateTodo() {
        CreateTodoRequest request = new CreateTodoRequest("Junit", "test");

        TodoResponse response = todoService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull().isPositive();
        assertThat(response.title()).isEqualTo("Junit");
        assertThat(response.description()).isEqualTo("test");
        assertThat(response.completed()).isFalse();
        assertThat(response.createdAt()).isNotNull();
    }


}

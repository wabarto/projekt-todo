package pl.kurs.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;
import pl.kurs.exception.TodoNotFoundException;
import pl.kurs.exception.ValidationException;
import pl.kurs.repository.InMemoryTodoRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.*;

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
        // given / arrange
        CreateTodoRequest request = new CreateTodoRequest("Junit", "test");

        // when / act
        TodoResponse response = todoService.create(request);

        // then / assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull().isPositive();
        assertThat(response.title()).isEqualTo("Junit");
        assertThat(response.description()).isEqualTo("test");
        assertThat(response.completed()).isFalse();
        assertThat(response.createdAt()).isNotNull();


        String name = "Anna Nowak";

        assertThat(name)
                .isNotBlank()
                .startsWith("Anna")
                .endsWith("Nowak")
                .hasSize(10)
                .isEqualToIgnoringCase("anna nowak");


        double price = 49.99;

        assertThat(price)
                .isPositive()
                .isGreaterThan(10.0)
                .isCloseTo(50.0, within(0.1));


        List<String> items = List.of("1", "2", "3");

        assertThat(items)
                .hasSize(3)
                .contains("1", "2")
                .doesNotContain("4")
                .containsExactly("1", "2", "3")
                .containsExactlyInAnyOrder("3", "1", "2")
                .anyMatch(s -> s.startsWith("1"))
                .noneMatch(String::isEmpty);



        assertThat(Optional.of("value"))
                .isPresent()
                .hasValue("value");

        assertThat(Optional.empty()).isEmpty();

        assertThat(response)
                .usingRecursiveAssertion()
                .ignoringFields("id", "createdAt")
                .isEqualTo(new TodoResponse(null, "test", "test", false, null, null));



    }

    @Test
    void shouldThrowWhenTodoNotFound() {
        TodoNotFoundException exception = assertThrows(TodoNotFoundException.class, () -> todoService.findById(999L));
        assertThat(exception.getMessage()).contains("999");
    }

    @Test
    void shouldThrowWhenTitleIsBlank() {
        // when/then
        assertThatThrownBy(() -> todoService.create(new CreateTodoRequest(" ", "test")))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Title cannot be blank");
    }

    @Test
    void shouldNotThrowForValidRequest() {
        assertThatNoException()
                .isThrownBy(() -> todoService.create(new CreateTodoRequest("1", "1")));
    }


    @Test
    void shouldVerifyAllFieldsAtOnce() {
        // given
        CreateTodoRequest request = new CreateTodoRequest("Junit", "test");

        // when
        TodoResponse response = todoService.create(request);

        // then

        assertAll("fields",
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.title()).isEqualTo("Junit"));
    }


    @Test
    void shouldFinishWithinReasonableTime() {
        assertTimeout(Duration.ofMillis(500), () -> todoService.create(new CreateTodoRequest("1", "1")));
    }


    @ParameterizedTest()
    @ValueSource(strings = {"", " "})
    void shouldDetectBlankStrings(String input) {
        assertThat(input.isBlank()).isTrue();
    }


    @ParameterizedTest
    @NullAndEmptySource
    void shouldRejectNullAndEmpty(String title) {
        assertThatThrownBy(() -> todoService.create(new CreateTodoRequest(title, "description")))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 4, 5",
            "5, 6, 7"
    })
    void shouldComputeSomething(int value1, int value2, int value3) {
        assertThat(value1).isGreaterThan(0);
        assertThat(value2).isGreaterThan(0);
        assertThat(value3).isGreaterThan(0);
    }


    static Stream<Arguments> titles() {
        return Stream.of(
                Arguments.of("title", "description", true),
                Arguments.of(" ", "description1", false)
        );
    }

    @ParameterizedTest
    @MethodSource("titles")
    void shouldAcceptOnlyNotBlankTitles(String title, String description, boolean shouldSucceed) {
        // given
        CreateTodoRequest request = new CreateTodoRequest(title, description);

        // when / then

        if (shouldSucceed) {
            assertThatNoException().isThrownBy(() -> todoService.create(request));
        } else {
            assertThatThrownBy(() -> todoService.create(request))
                    .isInstanceOf(ValidationException.class);
        }
    }






}

package pl.kurs;

import pl.kurs.controller.TodoController;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.repository.InMemoryTodoRepository;
import pl.kurs.repository.TodoRepository;
import pl.kurs.service.TodoService;
import pl.kurs.service.TodoServiceImpl;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        TodoRepository repository = new InMemoryTodoRepository();
        TodoService service = new TodoServiceImpl(repository);
        TodoController todoController = new TodoController(service);


        var request = new CreateTodoRequest("title", "description");
        var response = todoController.handleCreate(request);
        System.out.println(response);

        var all = todoController.handleGetAll(null);
        System.out.println(all);

        todoController.handleDelete(response.id());

        System.out.println(todoController.handleGetAll(null));
    }
}
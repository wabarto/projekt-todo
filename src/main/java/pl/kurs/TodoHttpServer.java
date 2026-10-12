package pl.kurs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import pl.kurs.controller.TodoController;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.repository.InMemoryTodoRepository;
import pl.kurs.repository.TodoRepository;
import pl.kurs.service.TodoService;
import pl.kurs.service.TodoServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TodoHttpServer {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRepository repository = new InMemoryTodoRepository();
        TodoService service = new TodoServiceImpl(repository);
        TodoController todoController = new TodoController(service, mapper);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/todos", todoController);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
    }
}
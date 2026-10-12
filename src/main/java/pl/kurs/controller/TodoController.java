package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.ErrorResponse;
import pl.kurs.exception.TodoNotFoundException;
import pl.kurs.exception.ValidationException;
import pl.kurs.service.TodoService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TodoController implements HttpHandler {

    private static final String BASE = "/api/todos";

    private final TodoService todoService;
    private final ObjectMapper objectMapper;

    public TodoController(TodoService todoService, ObjectMapper objectMapper) {
        this.todoService = todoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");

        String path = exchange.getRequestURI().getPath().substring(BASE.length());
        String method = exchange.getRequestMethod().toUpperCase();


        try {
            if (path.isEmpty() || path.equals("/")) { // /api/todos
                handleCollection(exchange, method);
            } else { // /api/todos/5
                handleSingle(exchange, method, path.substring(1));
            }
        } catch (Exception e) {
            handleFailure(exchange, e);
        }
    }

    private void handleFailure(HttpExchange exchange, Exception e) throws IOException {
        if (e instanceof TodoNotFoundException) {
            sendJson(exchange, 404, new ErrorResponse(e.getMessage(), "Not Found", 404));
        } else if (e instanceof ValidationException) {
            sendJson(exchange, 400, new ErrorResponse(e.getMessage(), "Bad Request", 400));
        } else if (e instanceof IllegalStateException) {
            sendJson(exchange, 409, new ErrorResponse(e.getMessage(), "Conflict", 409));
        } else {
            sendError(exchange, 500, e.getMessage(), "Internal Server Error");
        }
    }

    private void sendError(HttpExchange exchange, int status, String message, String error) throws IOException {
        sendJson(exchange, status, new ErrorResponse(message, error, status));
    }

    // /api/todos
    private void handleCollection(HttpExchange exchange, String method) throws IOException {
        switch(method) {
            case "GET" -> sendJson(exchange, 200, todoService.findAll(completedParam(exchange)));
            case "POST" -> {
                CreateTodoRequest request = readBody(exchange, CreateTodoRequest.class);
                var created = todoService.create(request);
                sendJson(exchange, 201, created);
            }
            default -> sendJson(exchange, 405, new ErrorResponse("Allowed: GET, POST", "Method not allowed", 405));
        }
    }

    // /api/todos/{id}
    private void handleSingle(HttpExchange exchange, String method, String rest) throws IOException {
        String[] segments = rest.split("/");
        Long id;

        try {
            id = Long.valueOf(segments[0]);
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, new ErrorResponse("Identifier has to be number", "Bad request", 400));
            return;
        }

        switch(method) {
            case "GET" -> sendJson(exchange, 200, todoService.findById(id));
            case "DELETE" -> {
                todoService.delete(id);
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
            }
            default -> sendJson(exchange, 405, new ErrorResponse("Allowed: GET, DELETE", "Method not allowed", 405));
        }
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return objectMapper.readValue(body, type);
    }

    private Boolean completedParam(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.startsWith("completed=")) {
            return null;
        }

        return Boolean.valueOf(query.substring("completed=".length()));
    }

    private void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] json = objectMapper.writeValueAsBytes(body);
        exchange.sendResponseHeaders(status, json.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(json);
        }

    }
}

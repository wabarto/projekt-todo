package pl.kurs.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.kurs.domain.Todo;
import pl.kurs.dto.CreateTodoRequest;
import pl.kurs.dto.TodoResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TodoApiClient {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String BASE = "http://localhost:8080/api/todos";

    public static void main(String[] args) throws IOException, InterruptedException {
        String json = MAPPER.writeValueAsString(new CreateTodoRequest("HTTP", "HTTP Client"));

        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create(BASE))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> created = CLIENT.send(post, HttpResponse.BodyHandlers.ofString());

        System.out.println("POST status= " + created.statusCode() + " header=" + created.headers().firstValue("Content-Type"));

        TodoResponse todo = MAPPER.readValue(created.body(), TodoResponse.class);

        System.out.println(todo);

        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/" + todo.id()))
                .header("Accept", "application/json")
                .GET()
                .build();


        HttpResponse<String> one = CLIENT.send(get, HttpResponse.BodyHandlers.ofString());

        TodoResponse fetched = MAPPER.readValue(one.body(), TodoResponse.class);

        System.out.println(fetched);

        HttpRequest getList = HttpRequest.newBuilder()
                .uri(URI.create(BASE))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> list = CLIENT.send(getList, HttpResponse.BodyHandlers.ofString());

        List<TodoResponse> todos = MAPPER.readValue(list.body(), new TypeReference<>() { });


        System.out.println(todos);

        HttpRequest getCompleted = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "?completed=true"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> completedList = CLIENT.send(getCompleted, HttpResponse.BodyHandlers.ofString());

        List<TodoResponse> completed = MAPPER.readValue(completedList.body(), new TypeReference<>() { });

        System.out.println(completed);

        HttpResponse<Void> deleted = CLIENT.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/999" + todo.id()))
                        .DELETE()
                        .build(), HttpResponse.BodyHandlers.discarding()
        );

        System.out.println(deleted.statusCode());



        List<Long> ids = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            HttpResponse<String> response = CLIENT.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(BASE))
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build(), HttpResponse.BodyHandlers.ofString()
            );

            ids.add(MAPPER.readValue(response.body(), TodoResponse.class).id());
        }


        List<CompletableFuture<TodoResponse>> futures = ids.stream()
                .map(id -> CLIENT.sendAsync(
                        HttpRequest.newBuilder()
                                .uri(URI.create(BASE + "/" + id))
                                .GET()
                                .build(), HttpResponse.BodyHandlers.ofString())
                        .thenApply(response -> {
                            if (response.statusCode() != 200) {
                                throw new IllegalStateException("HTTP " + response.statusCode());
                            }
                            try {
                                return MAPPER.readValue(response.body(), TodoResponse.class);
                            } catch (JsonProcessingException e) {
                                throw new IllegalStateException(e);
                            }
                        })).toList();


        List<TodoResponse> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).toList())
                        .join();

                all.forEach(t -> System.out.println("ASYNC id= " + t.id()));
    }
}

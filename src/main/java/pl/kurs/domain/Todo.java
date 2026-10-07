package pl.kurs.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Todo {

    private Long id;
    private final String title;
    private final String description;
    private boolean completed;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;


    public Todo(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    // metoda domenowa
    public void complete() {
        if (this.completed) {
            throw new IllegalStateException("already completed");
        }

        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    // identyfikator nadaje repo przy pierwszym zapisie

    public void assignId(Long newId) {
        if (this.id != null) {
            throw new IllegalStateException("id already assigned");
        }

        this.id = newId;
    }


}

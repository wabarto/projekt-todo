package pl.kurs.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(Long id) {
        super("Not found todo with id: " + id);
    }
}

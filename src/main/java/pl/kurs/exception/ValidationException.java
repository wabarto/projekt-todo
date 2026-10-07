package pl.kurs.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String mesage) {
        super(mesage);
    }
}

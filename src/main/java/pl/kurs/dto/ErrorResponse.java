package pl.kurs.dto;

public record ErrorResponse (String message, String error, int status) {
}

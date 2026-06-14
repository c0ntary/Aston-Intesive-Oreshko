package app.dto;

public record UserRequest(
        String name,
        String email,
        Integer age
) {
}
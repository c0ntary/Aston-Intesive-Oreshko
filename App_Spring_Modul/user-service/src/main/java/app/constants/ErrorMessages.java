package app.constants;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String INVALID_ID = "Некорректный ID";
    public static final String INVALID_USER = "Пользователь не может быть null";
    public static final String EMAIL_REQUIRED = "Email обязателен";
    public static final String EMAIL_EXISTS = "Пользователь с таким email уже существует";
    public static final String EMAIL_CYRILLIC_FORBIDDEN = "Email не должен содержать кириллицу";
    public static final String EMAIL_INVALID_FORMAT = "Некорректный формат email";
    public static final String NAME_REQUIRED = "Имя обязательно";
    public static final String AGE_INVALID = "Возраст должен быть положительным";
}

package app.validation;

import app.constants.ErrorMessages;
import app.dto.UserRequest;
import app.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern CYRILLIC_PATTERN =
            Pattern.compile(".*[А-Яа-яЁё].*");

    public void validateId(Long id) {
        if (id == null || id < 1) {
            throw new ValidationException(ErrorMessages.INVALID_ID);
        }
    }

    public void validate(UserRequest req) {
        if (req == null) {
            throw new ValidationException(ErrorMessages.INVALID_USER);
        }

        if (req.name() == null || req.name().isBlank()) {
            throw new ValidationException(ErrorMessages.NAME_REQUIRED);
        }

        if (req.email() == null || req.email().isBlank()) {
            throw new ValidationException(ErrorMessages.EMAIL_REQUIRED);
        }

        if (CYRILLIC_PATTERN.matcher(req.email()).matches()) {
            throw new ValidationException(ErrorMessages.EMAIL_CYRILLIC_FORBIDDEN);
        }

        if (!EMAIL_PATTERN.matcher(req.email()).matches()) {
            throw new ValidationException(ErrorMessages.EMAIL_INVALID_FORMAT);
        }

        if (req.age() == null || req.age() <= 0) {
            throw new ValidationException(ErrorMessages.AGE_INVALID);
        }
    }
}
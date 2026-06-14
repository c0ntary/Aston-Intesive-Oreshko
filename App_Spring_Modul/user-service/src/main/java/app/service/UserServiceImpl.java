package app.service;

import app.constants.ErrorMessages;
import app.dto.UserDto;
import app.dto.UserRequest;
import app.entity.User;
import app.exception.DuplicateEntityException;
import app.exception.EntityNotFoundException;
import app.mapper.UserMapper;
import app.notification.NotificationEventPublisher;
import app.repository.UserRepository;
import app.validation.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final NotificationEventPublisher eventPublisher;
    private final UserValidator validator;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repo,
                           NotificationEventPublisher eventPublisher,
                           UserValidator validator,
                           UserMapper mapper) {
        this.repo = repo;
        this.eventPublisher = eventPublisher;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public UserDto createUser(UserRequest request) {
        validator.validate(request);

        if (repo.existsByEmail(request.email())) {
            throw new DuplicateEntityException(ErrorMessages.EMAIL_EXISTS);
        }

        User saved = repo.save(mapper.toEntity(request));
        eventPublisher.publishUserCreated(saved.getEmail());

        return mapper.toDto(saved);
    }

    @Override
    public UserDto getUserById(Long id) {
        validator.validateId(id);

        User user = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        return mapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public UserDto updateUser(Long id, UserRequest request) {
        validator.validateId(id);
        validator.validate(request);

        User existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (!existing.getEmail().equals(request.email())
                && repo.existsByEmail(request.email())) {
            throw new DuplicateEntityException(ErrorMessages.EMAIL_EXISTS);
        }

        existing.setName(request.name());
        existing.setEmail(request.email());
        existing.setAge(request.age());

        User saved = repo.save(existing);
        eventPublisher.publishUserUpdated(saved.getEmail());

        return mapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        validator.validateId(id);

        User user = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        repo.delete(user);
        eventPublisher.publishUserDeleted(user.getEmail());
    }
}
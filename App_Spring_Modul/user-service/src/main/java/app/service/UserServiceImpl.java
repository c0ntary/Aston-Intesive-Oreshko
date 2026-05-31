package app.service;

import app.entity.User;
import app.exception.DuplicateEntityException;
import app.exception.EntityNotFoundException;
import app.exception.ValidationException;
import app.notification.NotificationEventPublisher;
import app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationEventPublisher eventPublisher;

    public UserServiceImpl(UserRepository userRepository,
                           NotificationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEntityException("Пользователь с таким email уже существует");
        }

        User saved = userRepository.save(user);
        eventPublisher.publishUserCreated(saved.getEmail());
        return saved;
    }

    @Override
    public User getUserById(Long id) {
        validateId(id);

        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User newData) {
        validateId(id);
        validateUser(newData);

        if (newData.getId() != null && !newData.getId().equals(id)) {
            throw new ValidationException("ID в теле запроса не совпадает с ID в пути");
        }

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (!existing.getEmail().equals(newData.getEmail())) {
            userRepository.findByEmail(newData.getEmail()).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new DuplicateEntityException("Пользователь с таким email уже существует");
                }
            });
        }

        existing.setName(newData.getName());
        existing.setEmail(newData.getEmail());
        existing.setAge(newData.getAge());

        User saved = userRepository.save(existing);

        eventPublisher.publishUserUpdated(saved.getEmail());

        return saved;
    }


    @Override
    public void deleteUser(Long id) {
        validateId(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        userRepository.delete(user);

        eventPublisher.publishUserDeleted(user.getEmail());
    }


    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new ValidationException("Некорректный ID");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("Пользователь не может быть null");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя обязательно");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email обязателен");
        }
        if (user.getAge() == null || user.getAge() <= 0) {
            throw new ValidationException("Возраст должен быть положительным");
        }
    }
}
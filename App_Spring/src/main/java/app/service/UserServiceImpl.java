package app.service;

import app.entity.User;
import app.exception.DuplicateEntityException;
import app.exception.EntityNotFoundException;
import app.exception.ValidationException;
import app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
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

    @Override
    public User getUserById(Long id) {
        validateId(id);

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        if (repository.existsByEmail(user.getEmail())) {
            throw new DuplicateEntityException("Email уже используется");
        }

        return repository.save(user);
    }

    @Override
    public User updateUser(Long id, User newData) {
        validateId(id);
        validateUser(newData);

        User existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        repository.findByEmail(newData.getEmail())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateEntityException("Email уже используется");
                });

        existing.setName(newData.getName());
        existing.setEmail(newData.getEmail());
        existing.setAge(newData.getAge());

        return repository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        validateId(id);

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        repository.deleteById(id);
    }
}
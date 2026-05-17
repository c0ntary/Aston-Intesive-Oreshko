package service;

import dao.UserDao;
import entity.User;
import exception.EntityNotFoundException;
import exception.ValidationException;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    // FIX: оставляем только DI-конструктор — он нужен для Mockito и для App
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(User user) {
        validateUser(user);
        userDao.create(user);
    }

    @Override
    public User getUserById(Long id) {
        validateId(id);
        return userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void updateUser(User user) {
        validateId(user.getId());
        validateUser(user);
        userDao.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        validateId(id);
        userDao.delete(id);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (user.getAge() <= 0) {
            throw new ValidationException("Возраст должен быть положительным");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID должен быть положительным");
        }
    }
}
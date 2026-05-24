package app.service;

import app.entity.User;
import app.exception.EntityNotFoundException;
import app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User createUser(User user) {
        return repo.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User updateUser(Long id, User newData) {
        User existing = getUserById(id);

        existing.setName(newData.getName());
        existing.setEmail(newData.getEmail());
        existing.setAge(newData.getAge());

        return repo.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        repo.deleteById(id);
    }
}
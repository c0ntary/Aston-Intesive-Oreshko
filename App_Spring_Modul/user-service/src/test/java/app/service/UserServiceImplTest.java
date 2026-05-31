package app.service;

import app.entity.User;
import app.exception.DuplicateEntityException;
import app.exception.EntityNotFoundException;
import app.exception.ValidationException;
import app.notification.NotificationEventPublisher;
import app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private NotificationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createUser_success() {
        User user = new User("test", "test@mail.com", 30);

        when(repo.existsByEmail("test@mail.com")).thenReturn(false);
        when(repo.save(user)).thenReturn(user);

        User result = service.createUser(user);

        assertEquals("test", result.getName());
        verify(repo).save(user);
    }

    @Test
    void createUser_duplicateEmail() {
        User user = new User("test", "test@mail.com", 30);

        when(repo.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.createUser(user));
    }

    @Test
    void createUser_invalidData() {
        User user = new User("", "mail", -1);

        assertThrows(ValidationException.class, () -> service.createUser(user));
    }

    @Test
    void getUserById_success() {
        User user = new User("test", "test@mail.com", 30);
        user.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(user));

        User result = service.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("test", result.getName());
    }

    @Test
    void getUserById_notFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getUserById(1L));
    }

    @Test
    void getUserById_invalidId() {
        assertThrows(ValidationException.class, () -> service.getUserById(0L));
    }

    @Test
    void getAllUsers_success() {
        User u1 = new User("A", "a@mail.com", 20);
        User u2 = new User("B", "b@mail.com", 25);

        when(repo.findAll()).thenReturn(List.of(u1, u2));

        List<User> list = service.getAllUsers();

        assertEquals(2, list.size());
    }

    @Test
    void updateUser_success() {
        User existing = new User("Old", "old@mail.com", 20);
        existing.setId(1L);

        User updated = new User("New", "new@mail.com", 30);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(repo.save(existing)).thenReturn(existing);

        User result = service.updateUser(1L, updated);

        assertEquals("New", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals(30, result.getAge());
    }

    @Test
    void updateUser_notFound() {
        User updated = new User("New", "new@mail.com", 30);

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateUser(1L, updated));
    }

    @Test
    void updateUser_duplicateEmail() {
        User existing = new User("Old", "old@mail.com", 20);
        existing.setId(1L);

        User updated = new User("New", "new@mail.com", 30);

        User other = new User("Other", "new@mail.com", 40);
        other.setId(2L);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.findByEmail("new@mail.com")).thenReturn(Optional.of(other));

        assertThrows(DuplicateEntityException.class, () -> service.updateUser(1L, updated));
    }

    @Test
    void updateUser_invalidId() {
        User updated = new User("New", "new@mail.com", 30);

        assertThrows(ValidationException.class, () -> service.updateUser(0L, updated));
    }

    @Test
    void deleteUser_success() {
        User user = new User("A", "a@mail.com", 20);
        user.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> service.deleteUser(1L));

        verify(repo).delete(user);
        verify(eventPublisher).publishUserDeleted("a@mail.com");
    }


    @Test
    void deleteUser_notFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteUser(1L));
    }

    @Test
    void deleteUser_invalidId() {
        assertThrows(ValidationException.class, () -> service.deleteUser(0L));
    }
}

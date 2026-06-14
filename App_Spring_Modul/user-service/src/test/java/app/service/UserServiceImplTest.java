package app.service;

import app.constants.ErrorMessages;
import app.dto.UserDto;
import app.dto.UserRequest;
import app.entity.User;
import app.exception.DuplicateEntityException;
import app.exception.EntityNotFoundException;
import app.exception.ValidationException;
import app.mapper.UserMapper;
import app.notification.NotificationEventPublisher;
import app.repository.UserRepository;
import app.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private NotificationEventPublisher publisher;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        UserRequest req = new UserRequest("test", "test@mail.com", 30);
        User entity = new User("test", "test@mail.com", 30);
        User saved = new User("test", "test@mail.com", 30);
        saved.setId(1L);

        UserDto dto = new UserDto(1L, "test", "test@mail.com", 30, saved.getCreatedAt());

        doNothing().when(validator).validate(req);
        when(repo.existsByEmail("test@mail.com")).thenReturn(false);
        when(mapper.toEntity(req)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        UserDto result = service.createUser(req);

        assertEquals("test", result.name());
        verify(publisher).publishUserCreated("test@mail.com");
    }

    @Test
    void createUser_duplicateEmail() {
        UserRequest req = new UserRequest("test", "test@mail.com", 30);

        doNothing().when(validator).validate(req);
        when(repo.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.createUser(req));
    }

    @Test
    void getUserById_success() {
        User user = new User("test", "test@mail.com", 30);
        user.setId(1L);

        UserDto dto = new UserDto(1L, "test", "test@mail.com", 30, LocalDateTime.now());

        doNothing().when(validator).validateId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(dto);

        UserDto result = service.getUserById(1L);

        assertEquals(1L, result.id());
    }

    @Test
    void getUserById_notFound() {
        doNothing().when(validator).validateId(1L);
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getUserById(1L));
    }

    @Test
    void getUserById_invalidId() {
        doThrow(new ValidationException(ErrorMessages.INVALID_ID))
                .when(validator).validateId(0L);

        assertThrows(ValidationException.class, () -> service.getUserById(0L));
    }

    @Test
    void getAllUsers_success() {
        User u1 = new User("A", "a@mail.com", 20);
        u1.setId(1L);

        User u2 = new User("B", "b@mail.com", 25);
        u2.setId(2L);

        UserDto dto1 = new UserDto(1L, "A", "a@mail.com", 20, LocalDateTime.now());
        UserDto dto2 = new UserDto(2L, "B", "b@mail.com", 25, LocalDateTime.now());

        when(repo.findAll()).thenReturn(List.of(u1, u2));
        when(mapper.toDto(u1)).thenReturn(dto1);
        when(mapper.toDto(u2)).thenReturn(dto2);

        List<UserDto> list = service.getAllUsers();

        assertEquals(2, list.size());
    }

    @Test
    void updateUser_success() {
        UserRequest req = new UserRequest("New", "new@mail.com", 40);

        User existing = new User("Old", "old@mail.com", 20);
        existing.setId(1L);

        User saved = new User("New", "new@mail.com", 40);
        saved.setId(1L);

        UserDto dto = new UserDto(1L, "New", "new@mail.com", 40, LocalDateTime.now());

        doNothing().when(validator).validateId(1L);
        doNothing().when(validator).validate(req);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("new@mail.com")).thenReturn(false);
        when(repo.save(existing)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        UserDto result = service.updateUser(1L, req);

        assertEquals("New", result.name());
    }

    @Test
    void updateUser_notFound() {
        UserRequest req = new UserRequest("New", "new@mail.com", 40);

        doNothing().when(validator).validateId(1L);
        doNothing().when(validator).validate(req);

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateUser(1L, req));
    }

    @Test
    void updateUser_duplicateEmail() {
        UserRequest req = new UserRequest("New", "new@mail.com", 40);

        User existing = new User("Old", "old@mail.com", 20);
        existing.setId(1L);

        doNothing().when(validator).validateId(1L);
        doNothing().when(validator).validate(req);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByEmail("new@mail.com")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.updateUser(1L, req));
    }

    @Test
    void deleteUser_success() {
        User user = new User("A", "a@mail.com", 20);
        user.setId(1L);

        doNothing().when(validator).validateId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> service.deleteUser(1L));

        verify(repo).delete(user);
        verify(publisher).publishUserDeleted("a@mail.com");
    }

    @Test
    void deleteUser_notFound() {
        doNothing().when(validator).validateId(1L);
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteUser(1L));
    }
}
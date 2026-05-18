package unit;

import dao.UserDao;
import entity.User;
import exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.UserService;
import service.UserServiceImpl;

import java.util.Optional;

class UserServiceTest {

    @Test
    void testCreateUser() {
        UserDao userDao = Mockito.mock(UserDao.class);
        UserService service = new UserServiceImpl(userDao);

        User user = new User("test", "test@mail.com", 25);
        service.createUser(user);

        Mockito.verify(userDao).create(Mockito.any(User.class));
        Assertions.assertEquals("test", user.getName());
    }

    @Test
    void testCreateUserInvalidAge() {
        UserDao userDao = Mockito.mock(UserDao.class);
        UserService service = new UserServiceImpl(userDao);

        User user = new User("test", "test@mail.com", -5);

        Assertions.assertThrows(ValidationException.class, () -> service.createUser(user));

    }

    @Test
    void testGetUser() {
        UserDao userDao = Mockito.mock(UserDao.class);
        UserService service = new UserServiceImpl(userDao);

        User user = new User("test2", "test2@mail.com", 30);

        Mockito.when(userDao.findById(1L)).thenReturn(Optional.of(user));

        User result = service.getUserById(1L);

        Assertions.assertEquals("test2", result.getName());
    }
}

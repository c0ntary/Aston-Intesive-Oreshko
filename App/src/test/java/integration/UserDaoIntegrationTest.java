package integration;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.HibernateUtil;

import java.util.Optional;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        userDao = new UserDaoImpl();
    }

    @BeforeEach
    void cleanDb() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @Order(1)
    void testCreateUser() {
        User user = new User("test", "test@mail.com", 20);
        userDao.create(user);

        Assertions.assertNotNull(user.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        User user = new User("test2", "test2@mail.com", 30);
        userDao.create(user);

        Optional<User> found = userDao.findById(user.getId());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("test2", found.get().getName());
    }

    @Test
    @Order(3)
    void testUpdateUser() {
        User user = new User("old", "old@mail.com", 22);
        userDao.create(user);

        user.setName("new");
        user.setAge(99);
        userDao.update(user);

        Optional<User> found = userDao.findById(user.getId());
        Assertions.assertEquals("new", found.get().getName());
        Assertions.assertEquals(99, found.get().getAge());
    }

    @Test
    @Order(4)
    void testDeleteUser() {
        User user = new User("delete", "delete@mail.com", 40);
        userDao.create(user);

        userDao.delete(user.getId());

        Optional<User> found = userDao.findById(user.getId());
        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    @Order(5)
    void testDuplicateEmail() {
        userDao.create(new User("A", "dup@mail.com", 20));

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userDao.create(new User("B", "dup@mail.com", 30))
        );
    }

    @Test
    @Order(6)
    void testDeleteNonExistingUser() {
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userDao.delete(999L)
        );
    }
}
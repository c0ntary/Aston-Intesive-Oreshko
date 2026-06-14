package app.repository;

import app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository repo;

    @BeforeEach
    void clean() {
        repo.deleteAll();
    }

    @Test
    void saveUser_success() {
        User user = new User("Test", "test@mail.com", 30);

        User saved = repo.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void findByEmail_success() {
        User user = new User("Test", "test@mail.com", 30);
        repo.save(user);

        Optional<User> found = repo.findByEmail("test@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test");
    }

    @Test
    void existsByEmail_success() {
        repo.save(new User("A", "a@mail.com", 20));

        boolean exists = repo.existsByEmail("a@mail.com");

        assertThat(exists).isTrue();
    }

    @Test
    void deleteUser_success() {
        User user = new User("A", "a@mail.com", 20);
        User saved = repo.save(user);

        repo.delete(saved);

        assertThat(repo.findById(saved.getId())).isEmpty();
    }
}
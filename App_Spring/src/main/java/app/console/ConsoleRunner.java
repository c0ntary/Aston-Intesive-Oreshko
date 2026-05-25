package app.console;

import app.dto.UserRequest;
import app.entity.User;
import app.mapper.UserMapper;
import app.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final UserService service;
    private final UserMapper mapper;

    public ConsoleRunner(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== User Service ===");
            System.out.println("1 — Создать пользователя");
            System.out.println("2 — Показать всех пользователей");
            System.out.println("3 — Найти пользователя по ID");
            System.out.println("4 — Обновить пользователя");
            System.out.println("5 — Удалить пользователя");
            System.out.println("0 — Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createUser(scanner);
                case "2" -> listUsers();
                case "3" -> getUser(scanner);
                case "4" -> updateUser(scanner);
                case "5" -> deleteUser(scanner);
                case "0" -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }
    }

    private void createUser(Scanner scanner) {
        System.out.println("\n=== Создание пользователя ===");

        String name = readNonEmptyString(scanner, "Введите имя: ");
        String email = readNonEmptyString(scanner, "Введите email: ");
        int age = readPositiveInt(scanner, "Введите возраст: ");

        UserRequest req = new UserRequest();
        req.setName(name);
        req.setEmail(email);
        req.setAge(age);

        try {
            User created = service.createUser(mapper.toEntity(req));
            System.out.println("Пользователь создан: " + created);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void listUsers() {
        System.out.println("\n=== Список пользователей ===");
        List<User> users = service.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("Пользователей нет.");
            return;
        }

        users.forEach(System.out::println);
    }

    private void getUser(Scanner scanner) {
        System.out.println("\n=== Поиск пользователя ===");

        long id = readPositiveLong(scanner, "Введите ID: ");

        try {
            User user = service.getUserById(id);
            System.out.println(user);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void updateUser(Scanner scanner) {
        System.out.println("\n=== Обновление пользователя ===");

        long id = readPositiveLong(scanner, "Введите ID: ");

        String name = readNonEmptyString(scanner, "Введите новое имя: ");
        String email = readNonEmptyString(scanner, "Введите новый email: ");
        int age = readPositiveInt(scanner, "Введите новый возраст: ");

        try {
            UserRequest req = new UserRequest();
            req.setName(name);
            req.setEmail(email);
            req.setAge(age);

            User updated = service.updateUser(id, mapper.toEntity(req));
            System.out.println("Обновлено: " + updated);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void deleteUser(Scanner scanner) {
        System.out.println("\n=== Удаление пользователя ===");

        long id = readPositiveLong(scanner, "Введите ID: ");

        try {
            service.deleteUser(id);
            System.out.println("Пользователь удалён.");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Поле не может быть пустым. Попробуйте снова.");
        }
    }

    private int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Введите положительное число.");
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }
    }

    private long readPositiveLong(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                long value = Long.parseLong(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Введите положительное число.");
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }
    }
}
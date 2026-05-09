import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": createUser(); break;
                    case "2": getUserById(); break;
                    case "3": listUsers(); break;
                    case "4": updateUser(); break;
                    case "5": deleteUser(); break;
                    case "0": running = false; break;
                    default: System.out.println("Неизвестная команда");
                }

            } catch (RuntimeException e) {
                System.out.println("\n=== ОШИБКА ===");
                System.out.println(e.getMessage());
                System.out.println("==============\n");
            }
        }

        HibernateUtil.shutdown();
    }

    private static void printMenu() {
        System.out.println("\n=== User Service ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void createUser() {

        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        Integer age = null;

        while (age == null) {
            System.out.print("Возраст (обязательно): ");
            String ageStr = scanner.nextLine();

            if (ageStr.isBlank()) {
                System.out.println("\nВозраст обязателен.");
                System.out.println("1 — ввести возраст");
                System.out.println("2 — вернуться в меню");
                System.out.print("Выберите действие: ");

                String choice = scanner.nextLine();
                if (choice.equals("2")) return;
                continue;
            }

            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                System.out.println("Возраст должен быть числом.");
            }
        }

        User user = new User(name, email, age);
        userDao.create(user);

        System.out.println("Пользователь создан: " + user);
    }

    private static void getUserById() {
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> userOpt = userDao.findById(id);

        userOpt.ifPresentOrElse(
                u -> System.out.println("Найден: " + u),
                () -> System.out.println("Пользователь не найден")
        );
    }

    private static void listUsers() {
        List<User> users = userDao.findAll();

        if (users.isEmpty()) {
            System.out.println("Пользователей нет");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {

        System.out.print("ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> userOpt = userDao.findById(id);

        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }

        User user = userOpt.get();

        System.out.print("Новое имя (пусто — оставить " + user.getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Новый email (пусто — оставить " + user.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Новый возраст (пусто — оставить " + user.getAge() + "): ");
        String ageStr = scanner.nextLine();

        if (!ageStr.isBlank()) {

            Integer age = null;

            while (age == null) {
                try {
                    age = Integer.parseInt(ageStr);
                } catch (NumberFormatException e) {
                    System.out.println("\nВозраст должен быть числом.");
                    System.out.println("1 — ввести возраст заново");
                    System.out.println("2 — вернуться в меню");
                    System.out.print("Выберите действие: ");

                    String choice = scanner.nextLine();
                    if (choice.equals("2")) return;

                    System.out.print("Введите возраст: ");
                    ageStr = scanner.nextLine();
                    continue;
                }
            }

            user.setAge(age);
        }

        userDao.update(user);

        System.out.println("Пользователь обновлён: " + user);
    }

    private static void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());

        userDao.delete(id);

        System.out.println("Операция удаления выполнена.");
    }
}
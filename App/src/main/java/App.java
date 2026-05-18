import entity.User;
import service.UserService;
import service.UserServiceImpl;
import dao.UserDaoImpl;

import java.util.Properties;
import java.util.Scanner;

public class App {

    private final UserService service = new UserServiceImpl(new UserDaoImpl());
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n=== User Service ===");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Найти пользователя по ID");
            System.out.println("3. Показать всех пользователей");
            System.out.println("4. Обновить пользователя");
            System.out.println("5. Удалить пользователя");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            int cmd = Integer.parseInt(scanner.nextLine());

            switch (cmd) {
                case 1 -> createUser();
                case 2 -> findUser();
                case 3 -> listUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> System.exit(0);
            }
        }
    }

    private void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        service.createUser(new User(name, email, age));
        System.out.println("Пользователь создан");
    }

    private void findUser() {
        System.out.print("ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.println(service.getUserById(id));
    }

    private void listUsers() {
        service.getAllUsers().forEach(System.out::println);
    }

    private void updateUser() {
        System.out.print("ID: ");
        long id = Long.parseLong(scanner.nextLine());

        User user = service.getUserById(id);

        System.out.print("Новое имя: ");
        user.setName(scanner.nextLine());

        System.out.print("Новый email: ");
        user.setEmail(scanner.nextLine());

        System.out.print("Новый возраст: ");
        user.setAge(Integer.parseInt(scanner.nextLine()));

        service.updateUser(user);
        System.out.println("Обновлено");
    }

    private void deleteUser() {
        System.out.print("ID: ");
        long id = Long.parseLong(scanner.nextLine());

        service.deleteUser(id);
        System.out.println("Удалено");
    }

    public static void main(String[] args) {
        new App().start();
    }
    static {
        try {
            Properties props = new Properties();
            props.load(App.class.getClassLoader().getResourceAsStream("application.properties"));

            System.setProperty("hibernate.connection.url", props.getProperty("hibernate.connection.url"));
            System.setProperty("hibernate.connection.username", props.getProperty("hibernate.connection.username"));
            System.setProperty("hibernate.connection.password", props.getProperty("hibernate.connection.password"));

        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить параметры подключения", e);
        }
    }

}

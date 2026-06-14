package app.notification;

import app.mail.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    private final MailService mailService;

    public NotificationListener(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(
            topics = "${kafka.topics.user-notifications}",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(UserNotificationEvent event) {

        System.out.println(">>> EVENT RECEIVED: " + event);
        System.out.println(">>> OPERATION: " + event.operation());
        System.out.println(">>> EMAIL: " + event.email());

        switch (event.operation()) {
            case CREATE -> mailService.sendAccountCreatedEmail(event.email());
            case UPDATE -> mailService.sendAccountUpdatedEmail(event.email());
            case DELETE -> mailService.sendAccountDeletedEmail(event.email());
            default -> System.out.println("Unknown event type: " + event.operation());
        }
    }
}
package app.notification;

import app.mail.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    private final MailService mailService;

    public NotificationListener(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = "user-notifications", groupId = "notification-service")
    public void handle(UserNotificationEvent event) {

        switch (event.getOperation()) {
            case "CREATE" -> mailService.sendAccountCreatedEmail(event.getEmail());
            case "UPDATE" -> mailService.sendAccountUpdatedEmail(event.getEmail());
            case "DELETE" -> mailService.sendAccountDeletedEmail(event.getEmail());
            default -> System.out.println("Unknown event type: " + event.getOperation());
        }
    }
}
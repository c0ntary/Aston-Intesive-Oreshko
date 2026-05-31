package app.notification;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventPublisher {

    private static final String TOPIC = "user-notifications";

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    public NotificationEventPublisher(KafkaTemplate<String, UserNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreated(String email) {
        kafkaTemplate.send(TOPIC, email, new UserNotificationEvent("CREATE", email));
    }

    public void publishUserUpdated(String email) {
        kafkaTemplate.send(TOPIC, email, new UserNotificationEvent("UPDATE", email));
    }

    public void publishUserDeleted(String email) {
        kafkaTemplate.send(TOPIC, email, new UserNotificationEvent("DELETE", email));
    }
}
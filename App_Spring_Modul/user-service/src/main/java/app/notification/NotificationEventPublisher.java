package app.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventPublisher {

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;
    private final String topic;

    public NotificationEventPublisher(
            KafkaTemplate<String, UserNotificationEvent> kafkaTemplate,
            @Value("${kafka.topics.user-notifications}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishUserCreated(String email) {
        kafkaTemplate.send(topic, email, new UserNotificationEvent("CREATE", email));
    }

    public void publishUserUpdated(String email) {
        kafkaTemplate.send(topic, email, new UserNotificationEvent("UPDATE", email));
    }

    public void publishUserDeleted(String email) {
        kafkaTemplate.send(topic, email, new UserNotificationEvent("DELETE", email));
    }

}
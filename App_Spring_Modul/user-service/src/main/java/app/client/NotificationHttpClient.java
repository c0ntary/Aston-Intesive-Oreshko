package app.client;

import app.notification.UserNotificationEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationHttpClient {

    private final WebClient client;

    public NotificationHttpClient(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://notification-service").build();
    }

    @CircuitBreaker(name = "notificationClient", fallbackMethod = "fallback")
    public void sendCreated(String email) {
        client.post()
                .uri("/api/notifications/created")
                .bodyValue(new UserNotificationEvent("CREATE", email))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void fallback(String email, Throwable t) {
        System.out.println("CircuitBreaker: notification-service недоступен, email=" + email);
    }
}
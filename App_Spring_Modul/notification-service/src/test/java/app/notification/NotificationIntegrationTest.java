package app.notification;

import app.NotificationServiceApplication;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = NotificationServiceApplication.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "user-notifications")
class NotificationIntegrationTest {

    private static GreenMail greenMail;

    @Autowired
    private KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @BeforeAll
    static void startMailServer() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
    }

    @AfterAll
    static void stopMailServer() {
        greenMail.stop();
    }

    @Test
    void whenUserCreatedEvent_thenEmailSent() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent();
        event.setOperation("CREATE");
        event.setEmail("test@mail.com");

        kafkaTemplate.send("user-notifications", event);

        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo("test@mail.com");
        assertThat(messages[0].getSubject()).contains("Аккаунт создан");
    }
}
package app.notification;

import app.NotificationServiceApplication;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = NotificationServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "user-notifications")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationIntegrationTest {

    private GreenMail greenMail;

    @Autowired
    private KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    @BeforeAll
    void startMailServer() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
    }

    @AfterAll
    void stopMailServer() {
        greenMail.stop();
    }

    @Test
    void whenUserCreatedEvent_thenEmailSent() {

        // record вместо POJO
        UserNotificationEvent event =
                new UserNotificationEvent("CREATE", "test@mail.com");

        kafkaTemplate.send("user-notifications", event);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    MimeMessage[] messages = greenMail.getReceivedMessages();
                    assertThat(messages).hasSize(1);
                    assertThat(messages[0].getAllRecipients()[0].toString())
                            .isEqualTo("test@mail.com");
                    assertThat(messages[0].getSubject())
                            .contains("Аккаунт создан");
                });
    }
}
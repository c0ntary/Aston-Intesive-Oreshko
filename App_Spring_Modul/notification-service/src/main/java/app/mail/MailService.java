package app.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAccountCreatedEmail(String email) {
        send(email, "Аккаунт создан", "Ваш аккаунт успешно создан.");
    }

    public void sendAccountUpdatedEmail(String email) {
        send(email, "Аккаунт обновлён", "Данные вашего аккаунта были успешно изменены.");
    }

    public void sendAccountDeletedEmail(String email) {
        send(email, "Аккаунт удалён", "Ваш аккаунт был удалён.");
    }

    private void send(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }
}
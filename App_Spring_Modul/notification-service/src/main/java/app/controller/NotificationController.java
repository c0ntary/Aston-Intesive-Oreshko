package app.controller;

import app.dto.MailRequestDto;
import app.mail.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final MailService mailService;

    public NotificationController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/created")
    public ResponseEntity<Void> sendCreated(@RequestBody MailRequestDto request) {
        mailService.sendAccountCreatedEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleted")
    public ResponseEntity<Void> sendDeleted(@RequestBody MailRequestDto request) {
        mailService.sendAccountDeletedEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
}
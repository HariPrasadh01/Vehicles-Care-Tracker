package com.example.vehiclecare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Always logs the reminder; also emails it when spring.mail.* and app.notify.email are configured.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String toEmail;
    private final String fromEmail;

    public NotificationService(ObjectProvider<JavaMailSender> mailSender,
                               @Value("${app.notify.email:}") String toEmail,
                               @Value("${spring.mail.username:noreply@vehicle-care.local}") String fromEmail) {
        this.mailSender = mailSender;
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
    }

    /** @return false if an email was attempted and failed. */
    public boolean send(String subject, String body) {
        log.info("{}\n{}", subject, body);

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || toEmail.isBlank()) {
            return true;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject(subject);
            msg.setText(body);
            sender.send(msg);
            return true;
        } catch (Exception e) {
            log.warn("Reminder email failed: {}", e.getMessage());
            return false;
        }
    }
}

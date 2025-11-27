package com.example.AsteroidAlertNotificationService.NotificationService.service;

import com.example.AsteroidAlertNotificationService.NotificationService.entity.Notification;
import com.example.AsteroidAlertNotificationService.NotificationService.repo.NotificationRepository;
import com.example.AsteroidAlertNotificationService.NotificationService.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmailService {


    private final NotificationRepository notiRepo;
    private final UserRepository userRepo;

    @Value("${email.sender}")
    private String sender;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(NotificationRepository notiRepo, UserRepository userRepo, JavaMailSender mailSender) {
        this.notiRepo = notiRepo;
        this.userRepo = userRepo;
        this.mailSender = mailSender;
    }

    @Async
    public void sendAlertEmail() {
        final String text = createEmailContent();
        if (text == null) {
            log.info("No new asteroid alert notification found");
        }

        final List<String> emails = userRepo.findAllEmailsAndNotificationEnabled();
        if (emails.isEmpty()) {
            log.info("No users to send notification email to.");
        }

        emails.forEach(email -> sendEmail(email, text));
        log.info("Emails sent to {} users.", emails.size());
    }

    private void sendEmail(String email, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(sender);
        message.setSubject("Asteroid Alert Notification");
        message.setText(text);
        mailSender.send(message);
    }

    private String createEmailContent() {
        // check to see if there are any asteroids to send alerts for
        List<Notification> notifications = notiRepo.findByEmailSent(false);
        if (notifications.isEmpty()) {
            return null;
        }

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("New Asteroid Alert Notification:\n");
        emailContent.append("============================================\n");

        notifications.forEach(notification -> {
            emailContent.append("Asteroid Name: " + notification.getAsteroidName() + "\n");
            emailContent.append("Close Approach Date: " + notification.getCloseApproachDate() + "\n");
            emailContent.append("Miss Distance in Kilometers: " + notification.getMissDistanceKilometers() + "\n");
            emailContent.append("Estimated Diameter Avg Meters: " + notification.getEstimatedDiameterAvgMeters() + "\n");
            emailContent.append("==================================================\n");
            notification.setEmailSent(true);
            notiRepo.save(notification);
        });
        return emailContent.toString();
    }

}

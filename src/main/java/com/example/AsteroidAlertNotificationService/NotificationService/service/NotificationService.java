package com.example.AsteroidAlertNotificationService.NotificationService.service;

import com.example.AsteroidAlertNotificationService.AsteroidCollisionAlert.event.AsteroidCollisionEvent;
import com.example.AsteroidAlertNotificationService.NotificationService.entity.Notification;
import com.example.AsteroidAlertNotificationService.NotificationService.repo.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notiRepo;
    private final EmailService emailService;

    @Autowired
    public NotificationService(NotificationRepository notiRepo, EmailService emailService) {
        this.notiRepo = notiRepo;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "AsteroidCollisionAlert", groupId = "notification-alert")
    public void alertEvent(AsteroidCollisionEvent notificationEvent) {
        log.info("Event received: {}", notificationEvent.toString());

        final Notification notification = Notification.builder()
                .asteroidName(notificationEvent.getAsteroidName())
                .closeApproachDate(LocalDate.parse(notificationEvent.getCloseApproachDate()))
                .estimatedDiameterAvgMeters(notificationEvent.getEstimatedDiameterAvgMeters())
                .missDistanceKilometers(new BigDecimal(notificationEvent.getMissDistanceKilometers()))
                .emailSent(false)
                .build();

        final Notification savedNotification = notiRepo.saveAndFlush(notification);
    }

    @Scheduled(fixedRate = 10000)
    public void sendEmail() {
        log.info("Sending scheduled email for dangerous asteroid alert...");
        emailService.sendAlertEmail();
    }
}

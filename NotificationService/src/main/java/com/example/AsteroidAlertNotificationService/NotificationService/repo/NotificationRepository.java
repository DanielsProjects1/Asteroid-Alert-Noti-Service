package com.example.AsteroidAlertNotificationService.NotificationService.repo;

import com.example.AsteroidAlertNotificationService.NotificationService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("")
    List<Notification> findByEmailSent(boolean b);
}

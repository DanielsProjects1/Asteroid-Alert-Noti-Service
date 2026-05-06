package com.example.AsteroidAlertNotificationService.NotificationService.repo;

import com.example.AsteroidAlertNotificationService.NotificationService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.email FROM User u WHERE u.notificationsEnabled = true")
    List<String> findAllEmailsAndNotificationEnabled();
}

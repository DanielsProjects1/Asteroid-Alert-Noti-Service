package com.example.asteroidalert.service;

import com.example.asteroidalert.client.NasaClient;
import com.example.asteroidalert.dto.Asteroid;
import com.example.asteroidalert.event.AsteroidCollisionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class AsteroidAlertService {
    private final NasaClient nasaClient;
    private final KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate;

    @Autowired
    public AsteroidAlertService(NasaClient nasaClient, KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate) {
        this.nasaClient = nasaClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void alert() {
        log.info("Alert service called.");

        final LocalDate from = LocalDate.now();
        final LocalDate to = LocalDate.now().plusDays(7);

        log.info("Getting asteroid list from: {}, to: {}", from, to);
        final List<Asteroid> asteroids = nasaClient.getNeoAsteroids(from, to);

        log.info("Found {} asteroids from {} to {}", asteroids.size(), from, to);

        final List<Asteroid> hazardousAsteroids = asteroids.stream()
                .filter(Asteroid::isPotentiallyHazardousAsteroid)
                .toList();

        log.info("Found {} potentially hazardous asteroids from {} to {}", hazardousAsteroids.size(), from, to);

        final List<AsteroidCollisionEvent> asteroidCollisionEventList = createEventListOfHazardousAsteroids(hazardousAsteroids);

        log.info("Sending {} asteroid alerts to Kafka", asteroidCollisionEventList.size());
        asteroidCollisionEventList.forEach(event -> {
            kafkaTemplate.send("asteroid-alerts", event);
            log.info("Asteroid alert sent to Kafka topic: {}", event);
        });
    }

    private List<AsteroidCollisionEvent> createEventListOfHazardousAsteroids(List<Asteroid> hazardousAsteroids) {
        return hazardousAsteroids.stream()
                .map(asteroid -> AsteroidCollisionEvent.builder()
                                .asteroidName(asteroid.getName())
                                .closeApproachDate(asteroid.getCloseApproachData().getFirst().getDate().toString())
                                .missDistanceKilometers(asteroid.getCloseApproachData().getFirst().getMissDistance().getKilometers())
                                .estimatedDiameterAvgMeters((asteroid.getEstimatedDiameter().getMeters().getMinDiameter() + asteroid.getEstimatedDiameter().getMeters().getMaxDiameter()) / 2)
                                .build()
                )
                .toList();
    }
}

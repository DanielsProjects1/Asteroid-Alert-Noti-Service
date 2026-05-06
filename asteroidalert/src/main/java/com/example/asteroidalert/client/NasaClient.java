package com.example.asteroidalert.client;

import com.example.asteroidalert.dto.Asteroid;
import com.example.asteroidalert.dto.NasaNeoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NasaClient {

    @Value("${nasa.neo.api.url}")
    private String nasaApiUrl;

    @Value("${nasa.api.key}")
    private String apiKey;

    public List<Asteroid> getNeoAsteroids(final LocalDate from, final LocalDate to) {
        final RestTemplate restTemplate = new RestTemplate();

        final NasaNeoResponse nasaNeoResponse = restTemplate.getForObject(getUrl(from, to), NasaNeoResponse.class);
        List<Asteroid> asteroids = new ArrayList<>();
        if (nasaNeoResponse != null) {
            asteroids.addAll(nasaNeoResponse.getNearEarthObjects().values().stream().flatMap(List::stream).toList());
        }
        return asteroids;
    }

    public String getUrl(final LocalDate from, final LocalDate to) {
        String apiUrl = UriComponentsBuilder.fromUriString(nasaApiUrl)
                .queryParam("start_date", from)
                .queryParam("end_date", to)
                .queryParam("api_key", apiKey)
                .toUriString();

        return apiUrl;
    }
}

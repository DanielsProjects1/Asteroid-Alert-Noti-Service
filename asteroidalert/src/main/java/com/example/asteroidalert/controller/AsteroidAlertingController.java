package com.example.asteroidalert.controller;

import com.example.asteroidalert.service.AsteroidAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/asteroid-alert")
class AsteroidAlertingController {
    private final AsteroidAlertService asteroidAlertService;

    @Autowired
    public AsteroidAlertingController(AsteroidAlertService asteroidAlertService) {
        this.asteroidAlertService = asteroidAlertService;
    }

    @PostMapping("alert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void alert() {
        this.asteroidAlertService.alert();
    }

}

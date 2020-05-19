package com.fr.fries.reference.controller;

import com.fr.fries.reference.config.WebConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

import static com.fr.fries.reference.common.Constants.*;

@RestController
@RequiredArgsConstructor
public class AdminController implements HealthIndicator {

    private final AtomicReference<Health> health = new AtomicReference<>(Health.up().build());
    private final WebConfig web;

    @Override
    public Health health() {
        return health.get();
    }

    @GetMapping(URI_HEALTH_CHECK)
    public ResponseEntity<Health> healthCheck() {
        HttpStatus status = health.get().getStatus().equals(Status.UP) ?
                HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(health.get(), status);
    }

    @PutMapping(URI_HEALTH_UP)
    public Health up() {
        Health up = Health.up().build();
        this.health.set(up);
        return health.get();
    }

    @PutMapping(URI_HEALTH_DOWN)
    public Health down() {
        Health down = Health.down().build();
        this.health.set(down);
        return health.get();
    }

    @PutMapping(URI_HEALTH_SHUTDOWN)
    public String shutdown() {
        if (health.get().getStatus().equals(Status.UP)) {
            return "The application is still running. Please stop the application first.";
        } else {
            web.shutdown();
            return "The application will be shut down.";
        }
    }
}

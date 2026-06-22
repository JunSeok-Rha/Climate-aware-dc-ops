package com.dcops.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();

        // Get overall health status
        HealthComponent health = healthEndpoint.health();
        String status = health.getStatus().getCode();
        response.put("status", status);

        // Get database health status
        String dbStatus = "UNKNOWN";
        if (health instanceof org.springframework.boot.actuate.health.CompositeHealth) {
            org.springframework.boot.actuate.health.CompositeHealth compositeHealth =
                (org.springframework.boot.actuate.health.CompositeHealth) health;
            HealthComponent dbComponent = compositeHealth.getComponents().get("db");
            if (dbComponent != null) {
                dbStatus = dbComponent.getStatus().getCode();
            }
        }
        response.put("db", dbStatus);

        // Add version
        response.put("version", "1.0.0");

        return response;
    }
}

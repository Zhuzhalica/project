package org.example.project.healthChecks;


import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Здесь можно добавить проверку подключения к БД
        boolean databaseIsUp = checkDatabaseConnection();
        if (databaseIsUp) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("Error", "Database connection is down").build();
        }
    }

    private boolean checkDatabaseConnection() {
        // Логика проверки подключения к БД
        return true;
    }
}
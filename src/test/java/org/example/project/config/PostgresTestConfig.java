package org.example.project.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

public class PostgresTestConfig {

    private static volatile PostgreSQLContainer<?> postgreSqlContainer = null;

    private static PostgreSQLContainer createContainer() {
        var instance = postgreSqlContainer;
        if (instance == null) {
            synchronized (PostgreSQLContainer.class) {
                instance = postgreSqlContainer;
                if (instance == null) {
                    postgreSqlContainer = instance = new PostgreSQLContainer<>("postgres:15.2")
                            .withDatabaseName("image_project")
                            .withUsername("postgres")
                            .withPassword("postgres")
                            .withStartupTimeout(Duration.ofSeconds(60))
                            .withReuse(true);
                    postgreSqlContainer.start();
                }
            }
        }

        return instance;
    }

    @Component("PostgresInitializer")
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            var postgreSQLContainer = createContainer();

            var jdbcUrl = postgreSQLContainer.getJdbcUrl();
            var username = postgreSQLContainer.getUsername();
            var password = postgreSQLContainer.getPassword();

            TestPropertyValues.of(
                    "spring.datasource.url=" + jdbcUrl,
                    "spring.datasource.username=" + username,
                    "spring.datasource.password=" + password,
                    "spring.datasource.driverClassName=org.postgresql.Driver"
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
package org.example.project.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.MinIOContainer;

import java.time.Duration;

public class MinioTestConfig {
    private static volatile MinIOContainer minioSqlContainer = null;

    private static MinIOContainer createContainer() {
        var instance = minioSqlContainer;
        if (instance == null) {
            synchronized (MinIOContainer.class) {
                instance = minioSqlContainer;
                if (instance == null) {
                    minioSqlContainer = instance = new MinIOContainer("minio/minio:latest")
                            .withUserName("user")
                            .withPassword("password")
                            .withStartupTimeout(Duration.ofSeconds(60))
                            .withReuse(true);
                    minioSqlContainer.start();
                }
            }
        }

        return instance;
    }

    @Component("MinioInitializer")
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            var minioContainer = createContainer();

            var url = minioContainer.getS3URL();
            var port = url.split(":")[2];
            var username = minioContainer.getUserName();
            var password = minioContainer.getPassword();

            TestPropertyValues.of(
                    "minio.url=" + url,
                    "minio.port=" + port,
                    "minio.accessKey=" + username,
                    "minio.secretKey=" + password
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}

package org.example.project.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {PostgresTestConfig.Initializer.class, MinioTestConfig.Initializer.class})
public abstract class BaseTest {
}
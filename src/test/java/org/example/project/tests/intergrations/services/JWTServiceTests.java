package org.example.project.tests.intergrations.services;


import org.example.project.config.BaseTest;
import org.example.project.services.JWTService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class JWTServiceTests extends BaseTest {
    @Autowired
    private JWTService jwtService;

    @Test
    public void getToken_ShouldReturnDifferentTokens_WithDifferentLogin() {
        var token1 = jwtService.getToken("login");
        var token2 = jwtService.getToken("login2");

        Assertions.assertNotEquals(token1, token2);
    }

    @Test
    public void getToken_ShouldReturnDifferentTokens_WithDifferentGetTime() throws InterruptedException {
        var token1 = jwtService.getToken("login");
        TimeUnit.SECONDS.sleep(1);
        var token2 = jwtService.getToken("login");

        Assertions.assertNotEquals(token1, token2);
    }
}

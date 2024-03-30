package org.example.project.tests.intergrations.services;

import org.example.project.config.BaseTest;
import org.example.project.dataModels.enums.Role;
import org.example.project.dataModels.models.User;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.repositories.UserRepository;
import org.example.project.services.user.UserService;
import org.example.project.utils.JWTUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class UserServiceTests extends BaseTest {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    @Autowired
    private JWTUtils jwtUtils;

    private User defaultUser;

    @BeforeEach
    @AfterEach
    void init() {
        repository.deleteAll();

        defaultUser = new User(null, "user@gmail.com", "12345678", Role.USER);
    }

    @Test
    public void authNewUser_ShouldSaveUser() {
        var password = defaultUser.getPassword();

        service.auth(defaultUser);

        Assertions.assertDoesNotThrow(() -> repository.findByLogin(defaultUser.getLogin()));
        var savedUser = repository.findByLogin(defaultUser.getLogin()).get();

        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertEquals(defaultUser.getRole(), savedUser.getRole());
        Assertions.assertNotEquals(password, savedUser.getPassword());
    }

    @Test
    public void authNewUser_ShouldSaveUser_WithRoleUser() {
        defaultUser.setRole(Role.ADMIN);

        service.auth(defaultUser);

        var savedUser = repository.findByLogin(defaultUser.getLogin()).get();
        Assertions.assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    public void authNewUser_ShouldReturnCorrectToken() {
        var authInfo = service.auth(defaultUser);

        Assertions.assertNotNull(authInfo.getToken());
        Assertions.assertEquals(defaultUser.getLogin(), jwtUtils.getLogin(authInfo.getToken()));
    }

    @Test
    public void authExistUser_ShouldReturnCorrectToken() {
        service.auth(defaultUser);

        var authInfo = service.auth(defaultUser);

        Assertions.assertNotNull(authInfo.getToken());
        Assertions.assertEquals(defaultUser.getLogin(), jwtUtils.getLogin(authInfo.getToken()));
    }

    @Test
    public void retryAuthUser_ShouldReturnNewToken() {
        var user = defaultUser;
        var authInfoFirst = service.auth(user);
        var authInfoSecond = service.auth(user);

        Assertions.assertNotEquals(authInfoFirst, authInfoSecond);
    }

    @Test
    public void getByLoginExistUser_ShouldReturnUser() {
        service.auth(defaultUser);

        var savedUser = service.getByLogin(defaultUser.getLogin());

        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertEquals(defaultUser.getLogin(), savedUser.getLogin());
        Assertions.assertEquals(defaultUser.getRole(), savedUser.getRole());
    }

    @Test
    public void getByLoginNotExistUser_ShouldThrowEntityNotFound() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> service.getByLogin("unknownUserLogin"), "Пользователя с login = unknownUserLogin не существует");
    }
}

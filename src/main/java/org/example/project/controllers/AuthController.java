package org.example.project.controllers;

import lombok.RequiredArgsConstructor;
import org.example.project.dataModels.dto.AuthenticateInfo;
import org.example.project.dataModels.dto.CreateUserDto;
import org.example.project.dataModels.mappers.UserMapper;
import org.example.project.services.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService service;
    private final UserMapper mapper = UserMapper.INSTANCE;

    @PostMapping
    public AuthenticateInfo auth(@RequestBody CreateUserDto request) {
        var user = mapper.toUserEntity(request);

        return service.auth(user);
    }
}

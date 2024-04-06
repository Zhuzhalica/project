package org.example.project.controllers;

import lombok.RequiredArgsConstructor;
import org.example.project.data.models.dto.AuthenticateInfo;
import org.example.project.data.models.dto.CreateUserDto;
import org.example.project.data.models.mappers.UserMapper;
import org.example.project.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authenticated in app.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final UserService service;
  private final UserMapper mapper = UserMapper.INSTANCE;

  /**
   * Sign in or login user.
   *
   * @param request users credentials
   * @return users jwt-token
   */
  @PostMapping
  public AuthenticateInfo auth(@RequestBody CreateUserDto request) {
    var user = mapper.toUserEntity(request);

    return service.auth(user);
  }
}

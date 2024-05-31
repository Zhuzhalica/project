package org.example.project.services;

import lombok.RequiredArgsConstructor;
import org.example.project.data.models.dto.AuthenticateInfo;
import org.example.project.data.models.enums.Role;
import org.example.project.data.models.models.User;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for work with user.
 */
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository repository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  /**
   * Sign in or login user.
   *
   * @param user user info
   * @return user info after authenticate
   */
  public AuthenticateInfo auth(User user) {
    var userCopy = new User(user);
    var originalPassword = userCopy.getPassword();
    var repUser = repository.findByLogin(userCopy.getLogin()).orElse(null);
    if (repUser == null) {
      userCopy.setRole(Role.USER);
      userCopy.setPassword(passwordEncoder.encode(userCopy.getPassword()));
      repUser = repository.save(userCopy);
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userCopy.getLogin(), originalPassword)
    );

    return new AuthenticateInfo(jwtService.getToken(repUser.getLogin()));
  }

  /**
   * Get user info by login.
   *
   * @param login user login
   * @return user info
   */
  public User getByLogin(String login) {
    return repository.findByLogin(login)
        .orElseThrow(() -> new EntityNotFoundException(
            "Пользователя с login = " + login + " не существует"));
  }
}

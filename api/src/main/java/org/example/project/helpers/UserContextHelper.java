package org.example.project.helpers;

import lombok.RequiredArgsConstructor;
import org.example.project.data.models.models.User;
import org.example.project.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Helper with frequent methods.
 */
@Component
@RequiredArgsConstructor
public class UserContextHelper {

  private final UserService userService;

  /**
   * Get user by request context.
   *
   * @return user info
   */
  public User getUserByRequestContext() {
    var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    return userService.getByLogin(userDetails.getUsername());
  }
}

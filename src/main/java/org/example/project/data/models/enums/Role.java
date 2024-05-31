package org.example.project.data.models.enums;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * User role in app.
 */
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String value;

  @Override
  public String getAuthority() {
    return value;
  }
}
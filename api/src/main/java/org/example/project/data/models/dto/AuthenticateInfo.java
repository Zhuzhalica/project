package org.example.project.data.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User info after authenticate.
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthenticateInfo {

  private String token;
}

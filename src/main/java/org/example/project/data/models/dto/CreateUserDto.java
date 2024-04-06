package org.example.project.data.models.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * User info for authenticate.
 */
@Getter
@Setter
public class CreateUserDto {

  private String login;

  private String password;
}

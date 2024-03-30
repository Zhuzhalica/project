package org.example.project.dataModels.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDto {
    private String login;

    private String password;
}

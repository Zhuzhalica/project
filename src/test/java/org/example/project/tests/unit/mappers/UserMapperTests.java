package org.example.project.tests.unit.mappers;

import org.example.project.data.models.dto.CreateUserDto;
import org.example.project.data.models.mappers.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserMapperTests {

  public static UserMapper mapper = UserMapper.INSTANCE;

  @Test
  public void toUserEntity() {
    var createUser = new CreateUserDto();
    createUser.setLogin("login");
    createUser.setPassword("password");

    var user = mapper.toUserEntity(createUser);

    Assertions.assertEquals(createUser.getLogin(), user.getLogin());
    Assertions.assertEquals(createUser.getPassword(), user.getPassword());
    Assertions.assertNull(user.getId());
    Assertions.assertNull(user.getRole());
  }

}

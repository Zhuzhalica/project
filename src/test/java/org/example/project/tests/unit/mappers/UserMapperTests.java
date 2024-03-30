package org.example.project.tests.unit.mappers;

import org.example.project.dataModels.dto.CreateUserDto;
import org.example.project.dataModels.mappers.ImageMapper;
import org.example.project.dataModels.mappers.UserMapper;
import org.example.project.dataModels.models.MetaImageInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

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

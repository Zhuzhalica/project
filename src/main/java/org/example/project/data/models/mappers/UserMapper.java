package org.example.project.data.models.mappers;


import org.example.project.data.models.dto.CreateUserDto;
import org.example.project.data.models.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Map user dto, models, etc.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  User toUserEntity(CreateUserDto userDto);
}

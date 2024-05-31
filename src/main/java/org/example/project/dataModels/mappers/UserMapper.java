package org.example.project.dataModels.mappers;


import org.example.project.dataModels.dto.CreateUserDto;
import org.example.project.dataModels.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUserEntity(CreateUserDto userDto);
}

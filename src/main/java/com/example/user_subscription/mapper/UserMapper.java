package com.example.user_subscription.mapper;

import com.example.user_subscription.dto.UserDto;
import com.example.user_subscription.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> users);
    User toUser(UserDto userDto);
}

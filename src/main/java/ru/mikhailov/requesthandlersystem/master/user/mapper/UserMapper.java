package ru.mikhailov.requesthandlersystem.master.user.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserAdminDto;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserDto;
import ru.mikhailov.requesthandlersystem.master.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);

    UserAdminDto toUserAdminDto(User user);
}

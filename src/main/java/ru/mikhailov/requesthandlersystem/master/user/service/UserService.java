package ru.mikhailov.requesthandlersystem.master.user.service;

import org.springframework.stereotype.Service;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserAdminDto;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    UserDto createUser(UserDto userDto);

    void deleteUserById(Long adminId, Long userId);

    List<UserAdminDto> getAllUsers(Long adminId, int from, int size);

    UserAdminDto getUserByNamePart(String namePart);

    UserAdminDto assignRightsOperator(Long adminId, Long userId);
}
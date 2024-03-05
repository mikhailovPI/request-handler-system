package ru.mikhailov.requesthandlersystem.master.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserDto;
import ru.mikhailov.requesthandlersystem.master.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping(path = "/registration/user")
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("URL: /users. PostMapping/Создание пользователя/createUser");
        return userService.createUser(userDto);
    }
}

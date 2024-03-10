package ru.mikhailov.requesthandlersystem.master.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserDto;
import ru.mikhailov.requesthandlersystem.master.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/registration")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("URL: /registration. PostMapping/Создание пользователя/createUser");
        return userService.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<?> showRegistrationForm() {
        return ResponseEntity.ok("Registration page is under construction.");
    }

}

package ru.mikhailov.requesthandlersystem.master.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserAdminDto;
import ru.mikhailov.requesthandlersystem.master.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = RequestAdminController.URL_ADMIN)
@Slf4j
public class RequestAdminController {

    public final static String URL_ADMIN = "/request/admin";

    private final UserService userService;

    //Посмотреть список всех пользователей
    @GetMapping(path = "/{adminId}/users")
    public List<UserAdminDto> getAllUsers(
            @PathVariable Long adminId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/admin/{adminId}/users. GetMapping/Получить всех пользователей/getAllUsers");
        return userService.getAllUsers(adminId, from, size);
    }

    //Поиск пользователя по имени
    @GetMapping(path = "/user")
    public UserAdminDto getUserByName(
            @RequestParam(name = "namePart", required = false) String namePart) {
        log.info("URL: /request/admin/user. GetMapping/Поиск пользователя по имени/getUserByName");
        return userService.getUserByName(namePart);
    }

    //Назначение прав оператора
    @PatchMapping(path = "/{adminId}/user/{userId}")
    public UserAdminDto assignRightsOperator(
            @PathVariable Long adminId,
            @PathVariable Long userId) {
        log.info("URL: /request/admin/{adminId}/user/{userId}. PatchMapping/Поиск пользователя " +
                "по имени/getUserByName");
        return userService.assignRightsOperator(adminId, userId);
    }

    //Удаление пользователя по id
    @DeleteMapping(path = "/{adminId}/user/{userId}")
    public void deleteUserById(
            @PathVariable Long adminId,
            @PathVariable Long userId) {
        log.info("URL: /request/admin/{adminId}/users/{userId}. DeleteMapping/Удаление пользователя с id: "
                + userId + "/deleteUserById");
        userService.deleteUserById(adminId, userId);
    }
}

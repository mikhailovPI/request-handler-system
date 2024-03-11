package ru.mikhailov.requesthandlersystem.master.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.model.RequestStatus;
import ru.mikhailov.requesthandlersystem.master.request.service.RequestService;
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
    private final RequestService requestService;

    //Посмотреть список всех пользователей
    @GetMapping(path = "/{adminId}/users")
    @PreAuthorize("hasAuthority('admin:write')")
    public List<UserAdminDto> getAllUsers(
            @PathVariable Long adminId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/admin/{adminId}/users. GetMapping/Получить всех пользователей/getAllUsers");
        return userService.getAllUsers(adminId, from, size);
    }

    //Поиск пользователя по имени
    @GetMapping(path = "/user")
    @PreAuthorize("hasAuthority('admin:write')")
    public UserAdminDto getUserByNamePart(
            @RequestParam(name = "namePart", required = false) String namePart) {
        log.info("URL: /request/admin/user. GetMapping/Поиск пользователя по имени/getUserByNamePart");
        return userService.getUserByNamePart(namePart);
    }

    //Назначение прав оператора
    @PatchMapping(path = "/{adminId}/user/{userId}")
    @PreAuthorize("hasAuthority('admin:write')")
    public UserAdminDto assignRightsOperator(
            @PathVariable Long adminId,
            @PathVariable Long userId) {
        log.info("URL: /request/admin/{adminId}/user/{userId}. PatchMapping/Назначение прав оператор " +
                "по имени/assignRightsOperator");
        return userService.assignRightsOperator(adminId, userId);
    }

    //Удаление пользователя по id
    @DeleteMapping(path = "/{adminId}/user/{userId}")
    @PreAuthorize("hasAuthority('admin:write')")
    public void deleteUserById(
            @PathVariable Long adminId,
            @PathVariable Long userId) {
        log.info("URL: /request/admin/{adminId}/users/{userId}. DeleteMapping/Удаление пользователя с id: "
                + userId + "/deleteUserById");
        userService.deleteUserById(adminId, userId);
    }

    //Посмотреть заявки
    @GetMapping(path = "/{adminId}/{sort}")
    @PreAuthorize("hasAuthority('admin:write')")
    public List<RequestAllDto> getAdminRequests(
            @PathVariable Long adminId,
            @RequestParam(name = "namePart", required = false, defaultValue = "") String namePart,
            @RequestParam List<RequestStatus> status,
            @PathVariable String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/admin/request. GetMapping/Получение админом заявок/getAdminRequests");
        return requestService.getAdminRequests(adminId, namePart, status, sort, from, size);
    }

}

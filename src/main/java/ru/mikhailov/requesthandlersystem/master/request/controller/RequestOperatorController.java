package ru.mikhailov.requesthandlersystem.master.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestDto;
import ru.mikhailov.requesthandlersystem.master.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = RequestOperatorController.URL_OPERATOR)
@Slf4j
public class RequestOperatorController {

    public final static String URL_OPERATOR = "/request/operator";
    private final RequestService requestService;

    //Получение всех заявок с возможностью сортировки по дате и пагинацией
    @GetMapping(path = "/{sort}")
    @PreAuthorize("hasAuthority('operator:write')")
    public List<RequestAllDto> getSippedRequests(
            @PathVariable String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/operator/{sort}. GetMapping/Получение всех заявок в статусе отправлено" +
                "/getSippedRequests");
        return requestService.getSippedRequests(sort, from, size);
    }

    //Получение всех заявок пользователя по его имени с возможностью сортировки по дате и пагинацией
    @GetMapping(path = "/users/{sort}")
    @PreAuthorize("hasAuthority('operator:write')")
    public List<RequestDto> getRequestsByNamePart(
            @RequestParam String namePart,
            @PathVariable String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/operator/users/{sort}. " +
                "GetMapping/Получение всех заявок пользователя по имени/getRequestsByNamePart");
        return requestService.getRequestsByNamePart(namePart, sort, from, size);
    }

    //Получение всех принятых заявок с возможностью сортировки по дате и пагинацией
    @GetMapping(path = "/accept/users/{sort}")
    @PreAuthorize("hasAuthority('operator:write')")
    public List<RequestAllDto> getAcceptedRequests(
            @PathVariable String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/operator/accept/users/{sort}. " +
                "GetMapping/Получение всех принятых заявок/getAcceptedRequests");
        return requestService.getAcceptedRequests(sort, from, size);
    }

    //Получение всех отклоненных заявок с возможностью сортировки по дате и пагинацией
    @GetMapping(path = "/reject/users/{sort}")
    @PreAuthorize("hasAuthority('operator:write')")
    public List<RequestAllDto> getRejectedRequests(
            @PathVariable String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        log.info("URL: /request/operator/reject/users/{sort}. " +
                "GetMapping/Получение всех отклоненных заявок/getRejectedRequests");
        return requestService.getRejectedRequests(sort, from, size);
    }

    //Принятие заявки
    @PatchMapping(path = "/{operatorId}/accept/{requestId}")
    @PreAuthorize("hasAuthority('operator:write')")
    public RequestAllDto acceptRequest(
            @PathVariable Long operatorId,
            @PathVariable Long requestId) {
        log.info("URL: /request/operator/{operatorId}/accept/{requestId}. " +
                "PatchMapping/Принятие заявки/acceptRequest");
        return requestService.acceptRequest(operatorId, requestId);
    }

    //Отклонение заявки
    @PatchMapping(path = "/{operatorId}/reject/{requestId}")
    @PreAuthorize("hasAuthority('operator:write')")
    public RequestAllDto rejectRequest(
            @PathVariable Long operatorId,
            @PathVariable Long requestId) {
        log.info("URL: /request/operator/{operatorId}/reject/{requestId}. " +
                "PatchMapping/Отклонение заявки/rejectRequest");
        return requestService.rejectRequest(operatorId, requestId);
    }
}

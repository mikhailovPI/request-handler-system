package ru.mikhailov.requesthandlersystem.master.request.service;

import org.springframework.stereotype.Service;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestNewDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestUpdateDto;
import ru.mikhailov.requesthandlersystem.master.request.model.RequestStatus;

import java.util.List;

@Service
public interface RequestService {

    //Методы для пользователя
    List<RequestDto> getRequestsByUser(Long userId, String sort, int from, int size);

    RequestAllDto createRequest(RequestNewDto request, Long userId);

    RequestDto sendRequest(Long userId, Long requestId);

    RequestDto updateRequest(Long userId, Long requestId, RequestUpdateDto requestUprateDto);

    //Методы для оператора
    List<RequestAllDto> getSippedRequests(String sort, int from, int size);

    List<RequestDto> getRequestsByNamePart(String namePart, String sort, int from, int size);

    List<RequestAllDto> getAcceptedRequests(String sort, int from, int size);

    List<RequestAllDto> getRejectedRequests(String sort, int from, int size);

    RequestAllDto acceptRequest(Long operatorId, Long requestId);

    RequestAllDto rejectRequest(Long operatorId, Long requestId);

    List<RequestAllDto> getAdminRequests(String namePart, List<RequestStatus> status,
                                            String sort, int from, int size);
}
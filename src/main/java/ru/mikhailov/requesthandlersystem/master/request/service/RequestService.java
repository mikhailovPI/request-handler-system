package ru.mikhailov.requesthandlersystem.master.request.service;

import org.springframework.stereotype.Service;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestNewDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestUpdateDto;

import java.util.List;

@Service
public interface RequestService {

    //Методы для пользователя
    List<RequestDto> getRequestsByUser(Long userId, Integer sort, int from, int size);

    RequestAllDto createRequest(RequestNewDto request, Long userId);

    RequestDto sendRequest(Long userId, Long requestId);

    RequestDto updateRequest(Long userId, Long requestId, RequestUpdateDto requestUprateDto);

    //Методы для оператора
    List<RequestAllDto> getRequests(Integer sort, int from, int size);

    List<RequestDto> getUserRequest(String namePart, Integer sort, int from, int size);

    List<RequestAllDto> getAcceptRequest(Integer sort, int from, int size);

    List<RequestAllDto> getRejectRequest(Integer sort, int from, int size);

    RequestAllDto acceptRequest(Long operatorId, Long requestId);

    RequestAllDto rejectRequest(Long operatorId, Long requestId);

}
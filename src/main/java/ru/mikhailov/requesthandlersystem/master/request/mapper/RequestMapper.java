package ru.mikhailov.requesthandlersystem.master.request.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestNewDto;
import ru.mikhailov.requesthandlersystem.master.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    Request toRequest(RequestNewDto requestDto);

    RequestDto toRequestDto(Request request);

    RequestAllDto toRequestAllDto(Request request);
}

package ru.mikhailov.requesthandlersystem.master.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.mikhailov.requesthandlersystem.master.request.model.RequestStatus;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserRequestDro;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestAllDto {

    Long id;

    String text;

    LocalDateTime publishedOn;

    UserRequestDro user;

    RequestStatus status;
}

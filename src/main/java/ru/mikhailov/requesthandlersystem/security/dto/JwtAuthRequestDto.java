package ru.mikhailov.requesthandlersystem.security.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtAuthRequestDto {

    String email;

    String password;
}
package ru.mikhailov.requesthandlersystem.master.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.mikhailov.requesthandlersystem.master.user.model.Role;
import ru.mikhailov.requesthandlersystem.master.user.model.Status;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    String name;

    String password;

    String email;

    Set<Role> userRole = new HashSet<>();

    Status status;
}

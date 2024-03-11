package ru.mikhailov.requesthandlersystem.master.config;


import org.springframework.beans.factory.annotation.Value;
import ru.mikhailov.requesthandlersystem.master.exception.NotFoundException;
import ru.mikhailov.requesthandlersystem.master.exception.ValidationException;
import ru.mikhailov.requesthandlersystem.master.user.model.Role;
import ru.mikhailov.requesthandlersystem.master.user.model.User;

import java.util.stream.Collectors;

public class Validation {


    @Value("${role.operator}")
    private String operatorRoleName;
    @Value("${role.user}")
    private String userRoleName;

    public static void validationBodyUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("E-mail не должен быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Введен некорректный e-mail.");
        }
        if (user.getName() == null) {
            throw new ValidationException("Имя не должен быть пустым.");
        }
        if (user.getPassword() == null) {
            throw new ValidationException("Пароль не должен быть пустым.");
        }
    }
}

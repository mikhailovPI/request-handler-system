package ru.mikhailov.requesthandlersystem.master.config;


import ru.mikhailov.requesthandlersystem.master.exception.ValidationException;
import ru.mikhailov.requesthandlersystem.master.user.model.User;

public class Validation {

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

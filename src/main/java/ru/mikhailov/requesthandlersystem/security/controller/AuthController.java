package ru.mikhailov.requesthandlersystem.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/auth/login")
public class AuthController {

    @GetMapping
    public String getLogin() {
        return "loginView";
    }
}
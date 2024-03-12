package ru.mikhailov.requesthandlersystem.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mikhailov.requesthandlersystem.exception.NotFoundException;
import ru.mikhailov.requesthandlersystem.master.user.model.Role;
import ru.mikhailov.requesthandlersystem.master.user.model.User;
import ru.mikhailov.requesthandlersystem.master.user.repository.UserRepository;
import ru.mikhailov.requesthandlersystem.security.config.JwtTokenProvider;
import ru.mikhailov.requesthandlersystem.security.dto.JwtAuthRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> authorization(
            @RequestBody JwtAuthRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("Пользователя с таким email не существует"));
            List<String> roles = user.getUserRole()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());


            String token = jwtTokenProvider.createToken(request.getEmail(), roles);
            Map<Object, Object> response = new HashMap<>();
            response.put("email", request.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Неверный комбинация password-email", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/auth/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }
}
package ru.mikhailov.requesthandlersystem.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.mikhailov.requesthandlersystem.exception.JwtAuthenticationException;
import ru.mikhailov.requesthandlersystem.exception.UnauthorizedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static ru.mikhailov.requesthandlersystem.master.user.controller.UserController.URL_REG;
import static ru.mikhailov.requesthandlersystem.security.config.SecurityConfig.URL_LOGIN;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String path = httpRequest.getServletPath();

        if (URL_LOGIN.equals(path) || URL_REG.equals(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw new UnauthorizedException("JWT Token просрочен или недействителен!");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
package ru.mikhailov.requesthandlersystem.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.mikhailov.requesthandlersystem.master.exception.NotFoundException;
import ru.mikhailov.requesthandlersystem.master.user.model.User;
import ru.mikhailov.requesthandlersystem.master.user.repository.UserRepository;
import ru.mikhailov.requesthandlersystem.security.model.UserSecurity;

@Component("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким email не существует"));
        return UserSecurity.fromUser(user);
    }
}

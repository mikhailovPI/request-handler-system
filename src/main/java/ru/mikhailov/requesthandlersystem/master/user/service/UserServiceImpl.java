package ru.mikhailov.requesthandlersystem.master.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikhailov.requesthandlersystem.master.config.PageRequestOverride;
import ru.mikhailov.requesthandlersystem.master.exception.ConflictingRequestException;
import ru.mikhailov.requesthandlersystem.master.exception.NotFoundException;
import ru.mikhailov.requesthandlersystem.master.request.repository.RequestRepository;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserAdminDto;
import ru.mikhailov.requesthandlersystem.master.user.dto.UserDto;
import ru.mikhailov.requesthandlersystem.master.user.mapper.UserMapper;
import ru.mikhailov.requesthandlersystem.master.user.model.User;
import ru.mikhailov.requesthandlersystem.security.config.Role;
import ru.mikhailov.requesthandlersystem.master.user.repository.RoleRepository;
import ru.mikhailov.requesthandlersystem.master.user.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.mikhailov.requesthandlersystem.master.config.Validation.validationBodyUser;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RequestRepository requestRepository;
    private final UserMapper userMapper;
    //private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        validationBodyUser(userMapper.toUser(userDto));
        User user = userMapper.toUser(userDto);
        if (userRepository.findByNameOrderByEmail()
                .stream()
                .noneMatch(email -> email.equals(userDto.getEmail()))) {
            Set<ru.mikhailov.requesthandlersystem.master.user.model.Role> roles = new HashSet<>();
            Set<ru.mikhailov.requesthandlersystem.master.user.model.Role> roleUserDto = userDto.getUserRole();

            if (roleRepository.findAll().isEmpty()) {
                user.setUserRole(roleUserDto);
                //user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user = userRepository.save(user);
                return userMapper.toUserDto(user);
            }
            for (ru.mikhailov.requesthandlersystem.master.user.model.Role role : roleUserDto) {
                ru.mikhailov.requesthandlersystem.master.user.model.Role roleFromDataBase = roleRepository.findByName(role.getName());
                if (roleFromDataBase != null) {
                    roles.add(roleFromDataBase);
                } else {
                    roles.add(role);
                }
            }
            user.setUserRole(roles);
            //user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user = userRepository.save(user);
        } else {
            throw new ConflictingRequestException(
                    String.format("Пользователь с email:  %s - уже существует!", userDto.getEmail()));
        }
        return userMapper.toUserDto(user);
    }

    //Методы для админа
    @Override
    public List<UserAdminDto> getAllUsers(Long adminId, int from, int size) {
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);
        User admin = validationUser(adminId);
        admin.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(String.valueOf(Role.ADMIN)))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может просматривать всех пользователей, " +
                                            "т.к. не является %s!",
                                    admin.getName(),
                                    admin.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    Role.ADMIN));
                });
        return userRepository.findAll(pageRequest)
                .stream()
                .map(userMapper::toUserAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserAdminDto getUserByName(String namePart) {
        return userMapper.toUserAdminDto(userRepository.findFirstUserByNamePart(namePart));
    }

    @Override
    @Transactional
    public UserAdminDto assignRightsOperator(Long adminId, Long userId) {
        User admin = validationUser(adminId);
        User user = validationUser(userId);
        admin.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(String.valueOf(Role.ADMIN)))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может назначить новую роль пользователю, " +
                                            "т.к. не является %s!",
                                    admin.getName(),
                                    admin.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    Role.ADMIN));
                });
        Set<ru.mikhailov.requesthandlersystem.master.user.model.Role> roleSet = new HashSet<>(
                Collections.singleton(
                        roleRepository.findByName(
                                String.valueOf(Role.OPERATOR))));
        if (user.getUserRole()
                .stream()
                .anyMatch(role -> role.getName().equals(String.valueOf(Role.USER)))) {
            user.setUserRole(roleSet);
        } else {
            throw new NotFoundException(
                    String.format("Пользователю %s нельзя назначить роль %s, т.к. он не является %s",
                            user.getName(),
                            Role.OPERATOR,
                            Role.USER));
        }
        userRepository.save(user);
        return userMapper.toUserAdminDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long adminId, Long userId) {
        User admin = validationUser(adminId);
        validationUser(userId);
        admin.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(String.valueOf(Role.ADMIN)))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может удалить пользователя, " +
                                            "т.к. не является %s!",
                                    admin.getName(),
                                    admin.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    Role.ADMIN));
                });
        requestRepository.deleteRequestsByUserId(userId);
        userRepository.deleteById(userId);
    }

    private User validationUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует!", userId)));
    }
}
package ru.mikhailov.requesthandlersystem.master.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikhailov.requesthandlersystem.master.config.PageRequestOverride;
import ru.mikhailov.requesthandlersystem.master.exception.NotFoundException;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestNewDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestUpdateDto;
import ru.mikhailov.requesthandlersystem.master.request.mapper.RequestMapper;
import ru.mikhailov.requesthandlersystem.master.request.model.Request;
import ru.mikhailov.requesthandlersystem.master.request.model.RequestStatus;
import ru.mikhailov.requesthandlersystem.master.request.repository.RequestRepository;
import ru.mikhailov.requesthandlersystem.master.user.model.User;
import ru.mikhailov.requesthandlersystem.master.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Value("${role.admin}")
    private String adminRoleName;
    @Value("${role.operator}")
    private String operatorRoleName;
    @Value("${role.user}")
    private String userRoleName;

    //Методы для пользователя
    @Override
    public List<RequestDto> getRequestsByUser(Long userId, Integer sort, int from, int size) {
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);
        validationUser(userId);
        if (sort.equals(0)) {
            //сортировка по возрастанию даты
            return requestRepository.findRequestsByUserId(userId, pageRequest)
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getPublishedOn().getNano()))
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else if (sort.equals(1)) {
            //сортировка по убыванию даты
            return requestRepository.findRequestsByUserId(userId, pageRequest)
                    .stream()
                    .sorted((o1, o2) -> o2.getPublishedOn().getNano() - o1.getPublishedOn().getNano())
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Сортировка возможна только по возрастанию или убыванию!");
        }
    }

    @Override
    @Transactional
    public RequestAllDto createRequest(RequestNewDto requestDto, Long userId) {
        User user = validationUser(userId);
        user.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(userRoleName))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может создавать заявку, т.к. не является %s!",
                                    user.getName(),
                                    user.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    userRoleName));
                });
        Request request = requestMapper.toRequest(requestDto);
        request.setPublishedOn(LocalDateTime.now());
        request.setUser(user);
        return requestMapper.toRequestAllDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto sendRequest(Long userId, Long requestId) {
        Request request = validationRequest(requestId);
        User user = validationUser(userId);
        if (!request.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь %s не может отправить чужую заявку!", user.getName()));
        }
        user.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(userRoleName))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может отправить заявку," +
                                            " т.к. не является %s!",
                                    user.getName(),
                                    user.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    userRoleName));
                });
        if (request.getStatus().equals(RequestStatus.DRAFT)) {
            request.setStatus(RequestStatus.SHIPPED);
            return requestMapper.toRequestDto(requestRepository.save(request));
        } else {
            throw new NotFoundException(
                    String.format("Заявка имеет статус %s, а должна иметь статус '%s'!",
                            request.getStatus(),
                            RequestStatus.DRAFT));
        }
    }

    @Override
    public RequestDto updateRequest(Long userId, Long requestId, RequestUpdateDto requestUprateDto) {
        Request request = validationRequest(requestId);
        User user = validationUser(userId);
        user.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(userRoleName))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может редактировать заявку, " +
                                            "т.к. не является %s!",
                                    user.getName(),
                                    user.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    userRoleName));
                });
        if (!request.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь %s не может редактировать чужую заявку!", user.getName()));
        }
        if (!request.getStatus().equals(RequestStatus.DRAFT)) {
            throw new NotFoundException(
                    String.format("Статус заявки не позволяет ее редактировать. Должен быть статус - '%s'!",
                            RequestStatus.REJECTED));
        }
        request.setText(requestUprateDto.getText());
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    //Методы для оператора
    @Override
    public List<RequestAllDto> getRequests(Integer sort, int from, int size) {
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);
        if (sort.equals(0)) {
            //сортировка по убыванию даты
            return requestRepository.findRequestStatusShipped(pageRequest)
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getPublishedOn().getNano()))
                    .map(requestMapper::toRequestAllDto)
                    .collect(Collectors.toList());
        } else if (sort.equals(1)) {
            //сортировка по возрастанию даты
            return requestRepository.findRequestStatusShipped(pageRequest)
                    .stream()
                    .sorted((o1, o2) -> o2.getPublishedOn().getNano() - o1.getPublishedOn().getNano())
                    .map(requestMapper::toRequestAllDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Сортировка возможна только по возрастанию или убыванию!");
        }
    }

    @Override
    public List<RequestDto> getUserRequest(String namePart, Integer sort, int from, int size) {
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);
        if (sort.equals(0)) {
            //сортировка по убыванию даты
            return requestRepository.findOrdersByUserNamePart(namePart, pageRequest)
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getPublishedOn().getNano()))
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else if (sort.equals(1)) {
            //сортировка по возрастанию даты
            return requestRepository.findOrdersByUserNamePart(namePart, pageRequest)
                    .stream()
                    .sorted((o1, o2) -> o2.getPublishedOn().getNano() - o1.getPublishedOn().getNano())
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException(
                    "Сортировка возможна только по возрастанию или убыванию!");
        }
    }

    @Override
    public List<RequestAllDto> getAcceptRequest(Integer sort, int from, int size) {
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);
        if (sort.equals(0)) {
            //сортировка по убыванию даты
            return requestRepository.findRequestStatusAccepted(pageRequest)
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getPublishedOn().getNano()))
                    .map(requestMapper::toRequestAllDto)
                    .collect(Collectors.toList());
        } else if (sort.equals(1)) {
            //сортировка по возрастанию даты
            return requestRepository.findRequestStatusAccepted(pageRequest)
                    .stream()
                    .sorted((o1, o2) -> o2.getPublishedOn().getNano() - o1.getPublishedOn().getNano())
                    .map(requestMapper::toRequestAllDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Сортировка возможна только по возрастанию или убыванию!");
        }
    }

    @Override
    public List<RequestAllDto> getRejectRequest(Integer sort, int from, int size) {
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);
        if (sort.equals(0)) {
            //сортировка по убыванию даты
            return requestRepository.findRequestStatusRejected(pageRequest)
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getPublishedOn().getNano()))
                    .map(requestMapper::toRequestAllDto)
                    .collect(Collectors.toList());
        } else if (sort.equals(1)) {
            //сортировка по возрастанию даты
            return requestRepository.findRequestStatusRejected(pageRequest)
                    .stream()
                    .sorted((o1, o2) -> o2.getPublishedOn().getNano() - o1.getPublishedOn().getNano())
                    .map(requestMapper::toRequestAllDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Сортировка возможна только по возрастанию или убыванию!");
        }
    }

    @Override
    @Transactional
    public RequestAllDto acceptRequest(Long operatorId, Long requestId) {
        Request request = validationRequest(requestId);
        User user = validationUser(operatorId);
        user.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(operatorRoleName))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s) не может принимать заявку, " +
                                            "т.к. не является %s!",
                                    user.getName(),
                                    user.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    operatorRoleName));
                });
        if (request.getStatus().equals(RequestStatus.SHIPPED)) {
            request.setStatus(RequestStatus.ACCEPTED);
            return requestMapper.toRequestAllDto(requestRepository.save(request));
        } else {
            throw new NotFoundException(
                    String.format("Заявка не имеет статус %s!", RequestStatus.SHIPPED));
        }
    }

    @Override
    @Transactional
    public RequestAllDto rejectRequest(Long operatorId, Long requestId) {
        Request request = validationRequest(requestId);
        User user = validationUser(operatorId);
        user.getUserRole()
                .stream()
                .filter(role -> !role.getName().equals(operatorRoleName))
                .forEach(role -> {
                    throw new NotFoundException(
                            String.format("Пользователь %s (роль - %s)не может отклонять заявку, " +
                                            "т.к. не является %s!",
                                    user.getName(),
                                    user.getUserRole()
                                            .stream()
                                            .map(ru.mikhailov.requesthandlersystem.master.user.model.Role::getName)
                                            .collect(Collectors.toSet()),
                                    operatorRoleName));
                });
        if (request.getStatus().equals(RequestStatus.SHIPPED) ||
                request.getStatus().equals(RequestStatus.ACCEPTED)) {
            request.setStatus(RequestStatus.REJECTED);
            return requestMapper.toRequestAllDto(requestRepository.save(request));
        } else {
            throw new NotFoundException(
                    String.format("Заявка не имеет статус %s или %s!",
                            RequestStatus.SHIPPED,
                            RequestStatus.ACCEPTED));
        }
    }

    //Методы валидации переданного id
    private User validationUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует!", userId)));
    }

    private Request validationRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Запрос %s не существует!", requestId)));
    }
}

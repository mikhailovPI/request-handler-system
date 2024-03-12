package ru.mikhailov.requesthandlersystem.master.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mikhailov.requesthandlersystem.exception.NotFoundException;
import ru.mikhailov.requesthandlersystem.master.config.PageRequestOverride;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestNewDto;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestUpdateDto;
import ru.mikhailov.requesthandlersystem.master.request.mapper.RequestMapper;
import ru.mikhailov.requesthandlersystem.master.request.model.Request;
import ru.mikhailov.requesthandlersystem.master.request.model.RequestStatus;
import ru.mikhailov.requesthandlersystem.master.request.repository.RequestRepository;
import ru.mikhailov.requesthandlersystem.master.user.model.Role;
import ru.mikhailov.requesthandlersystem.master.user.model.User;
import ru.mikhailov.requesthandlersystem.master.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByUser(Long userId, String sort, int from, int size) {
        validationUser(userId);
        validateSortParameter(sort);

        Sort publishedOn = Sort.by(Sort.Direction.fromString(sort), "publishedOn");
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size, publishedOn);

        return requestRepository.findRequestsByUserId(userId, pageRequest)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestAllDto createRequest(RequestNewDto requestDto, Long userId) {
        User user = validationUser(userId);
        validationUserRole(user);

        Request request = requestMapper.toRequest(requestDto);
        request.setPublishedOn(LocalDateTime.now());
        request.setUser(user);
        request.setStatus(RequestStatus.DRAFT);

        return requestMapper.toRequestAllDto(requestRepository.save(request));
    }

    @Override
    public RequestDto sendRequest(Long userId, Long requestId) {
        Request request = validationRequest(requestId);
        User user = validationUser(userId);

        if (!request.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь %s не может отправить чужую заявку!",
                            user.getName()));
        }

        validationUserRole(user);

        if (request.getStatus().equals(RequestStatus.DRAFT)) {
            request.setStatus(RequestStatus.SHIPPED);
            return requestMapper.toRequestDto(requestRepository.save(request));
        } else {
            throw new NotFoundException(String.format("Заявка имеет статус %s, а должна иметь статус '%s'!",
                    request.getStatus(),
                    RequestStatus.DRAFT));
        }
    }

    @Override
    public RequestDto updateRequest(Long userId, Long requestId, RequestUpdateDto requestUpdateDto) {
        Request request = validationRequest(requestId);
        User user = validationUser(userId);
        validationUserRole(user);

        if (!request.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь %s не может редактировать чужую заявку!",
                            user.getName()));
        }

        if (!request.getStatus().equals(RequestStatus.DRAFT)) {
            throw new NotFoundException(
                    String.format("Статус заявки не позволяет ее редактировать. Должен быть статус - '%s'!",
                            RequestStatus.DRAFT));
        }

        request.setText(requestUpdateDto.getText());
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    //Методы для оператора
    @Override
    @Transactional(readOnly = true)
    public List<RequestAllDto> getSippedRequests(String sort, int from, int size) {
        validateSortParameter(sort);

        Sort publishedOn = Sort.by(Sort.Direction.fromString(sort), "publishedOn");
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size, publishedOn);

        return requestRepository.findRequestStatusShipped(pageRequest)
                .stream()
                .map(requestMapper::toRequestAllDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByNamePart(String namePart, String sort, int from, int size) {
        validateSortParameter(sort);

        Sort publishedOn = Sort.by(Sort.Direction.fromString(sort), "publishedOn");
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size, publishedOn);

        return requestRepository.findShippedRequestsByUserNamePart(namePart, pageRequest)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestAllDto> getAcceptedRequests(String sort, int from, int size) {
        validateSortParameter(sort);

        Sort publishedOn = Sort.by(Sort.Direction.fromString(sort), "publishedOn");
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size, publishedOn);

        return requestRepository.findRequestStatusAccepted(pageRequest)
                .stream()
                .map(requestMapper::toRequestAllDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestAllDto> getRejectedRequests(String sort, int from, int size) {
        validateSortParameter(sort);

        Sort publishedOn = Sort.by(Sort.Direction.fromString(sort), "publishedOn");
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size, publishedOn);

        return requestRepository.findRequestStatusRejected(pageRequest)
                .stream()
                .map(requestMapper::toRequestAllDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestAllDto acceptRequest(Long operatorId, Long requestId) {
        Request request = validationRequest(requestId);
        User user = validationUser(operatorId);
        validationOperatorRole(user);

        if (!request.getStatus().equals(RequestStatus.SHIPPED)) {
            throw new NotFoundException(
                    String.format("Заявка не имеет статус %s!",
                            RequestStatus.SHIPPED));
        }

        request.setStatus(RequestStatus.ACCEPTED);
        return requestMapper.toRequestAllDto(requestRepository.save(request));
    }

    @Override
    public RequestAllDto rejectRequest(Long operatorId, Long requestId) {
        Request request = validationRequest(requestId);
        User user = validationUser(operatorId);
        validationOperatorRole(user);

        boolean a = request.getStatus().equals(RequestStatus.SHIPPED);
        boolean b = request.getStatus().equals(RequestStatus.ACCEPTED);

        if (!(a ||
                b)) {
            throw new NotFoundException(
                    String.format("Заявка не имеет статус %s или %s!",
                            RequestStatus.SHIPPED,
                            RequestStatus.ACCEPTED));
        }
        request.setStatus(RequestStatus.REJECTED);
        return requestMapper.toRequestAllDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestAllDto> getAdminRequests(
            Long adminId,
            String namePart,
            List<RequestStatus> status,
            String sort,
            int from,
            int size) {
        User admin = validationUser(adminId);
        validationAdminRole(admin);
        validateSortParameter(sort);

        Sort publishedOn = Sort.by(Sort.Direction.fromString(sort), "publishedOn");
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size, publishedOn);

        List<Request> requests = requestRepository.findByStatusAndUserNamePart(namePart, status, pageRequest);
        return requests.stream()
                .map(requestMapper::toRequestAllDto)
                .collect(Collectors.toList());
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

    private void validateSortParameter(String sort) {
        if (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Некорректное значение для параметра sort: " + sort);
        }
    }

    private void validationOperatorRole(User user) {
        boolean hasUserRole = user.getUserRole().stream()
                .anyMatch(role -> role.getName().equals(operatorRoleName));
        if (!hasUserRole) {
            throw new NotFoundException(
                    String.format("Пользователь %s (роль - %s)не может принимать заявку, " +
                                    "т.к. не является %s!",
                            user.getName(),
                            user.getUserRole().stream()
                                    .map(Role::getName)
                                    .collect(Collectors.joining(", ")),
                            operatorRoleName));
        }
    }

    private void validationUserRole(User user) {
        boolean hasUserRole = user.getUserRole()
                .stream()
                .anyMatch(role -> role.getName().equals(userRoleName));

        if (!hasUserRole) {
            throw new NotFoundException(
                    String.format("Пользователь %s (роль - %s) не может создавать заявку, т.к. не является %s!",
                            user.getName(),
                            user.getUserRole().stream()
                                    .map(Role::getName)
                                    .collect(Collectors.joining(", ")),
                            userRoleName));
        }
    }

    private void validationAdminRole(User admin) {
        boolean isAdmin = admin.getUserRole()
                .stream()
                .anyMatch(role -> role.getName().equals(adminRoleName));
        if (!isAdmin) {
            throw new NotFoundException(
                    String.format(
                            "Пользователь %s (роль - %s) не может назначить новую роль пользователю, " +
                                    "т.к. не является %s!",
                            admin.getName(),
                            admin.getUserRole().stream()
                                    .map(Role::getName)
                                    .collect(Collectors.joining(", ")),
                            adminRoleName));
        }
    }
}

package ru.mikhailov.requesthandlersystem.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;
import ru.mikhailov.requesthandlersystem.master.request.mapper.RequestMapper;
import ru.mikhailov.requesthandlersystem.master.request.model.Request;
import ru.mikhailov.requesthandlersystem.master.request.repository.RequestRepository;

@Service
public class KafkaConsumerService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestMapper requestMapper;

    @KafkaListener(topics = "requestTopic", groupId = "group_id",
            properties = {"spring.json.value.default.type=ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto"})
    public void consume(RequestAllDto requestDto) {
        Request request = requestMapper.toRequestAll(requestDto);
        requestRepository.save(request);
        System.out.println("Использованный и сохраненный запрос: " + request);
    }
}
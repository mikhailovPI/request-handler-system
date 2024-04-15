package ru.mikhailov.requesthandlersystem.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.mikhailov.requesthandlersystem.master.request.dto.RequestAllDto;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, RequestAllDto> kafkaTemplate;

    public void sendMessage(String topic, RequestAllDto message) {
        kafkaTemplate.send(topic, message);
    }
}
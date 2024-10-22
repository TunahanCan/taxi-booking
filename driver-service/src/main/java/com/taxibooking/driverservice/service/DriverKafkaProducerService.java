package com.taxibooking.driverservice.service;


import com.taxibooking.driverservice.model.DriverStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverKafkaProducerService {

    @Value("${driver.stage.topic}")
    private String driverStageTopic;

    private final KafkaTemplate<String, DriverStageDTO> kafkaTemplateForDriverStage;

    public void sendDriverStage(DriverStageDTO driverStageDTO, String headerMessage) {
        Message<DriverStageDTO> message = MessageBuilder
                .withPayload(driverStageDTO)
                .setHeader(KafkaHeaders.TOPIC, driverStageTopic)
                .setHeader("driver-stage-message", headerMessage)
                .build();
        kafkaTemplateForDriverStage.send(message);
    }
}

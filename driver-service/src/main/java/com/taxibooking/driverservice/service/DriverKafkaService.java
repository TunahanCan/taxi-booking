package com.taxibooking.driverservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxibooking.driverservice.model.DriverAssignRequestDTO;
import com.taxibooking.driverservice.model.DriverAssignResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverKafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${driver.assigned.topic}")
    private String driverAssignedTopic;

    public void assignDriver(DriverAssignRequestDTO assignRequest) throws JsonProcessingException {
        // Şoför atama işlemi (simüle)
        DriverAssignResponseDTO assignResponse = new DriverAssignResponseDTO();
        assignResponse.setBookingId(assignRequest.getBookingId());
        assignResponse.setDriverId("DRIVER_" + (int) (Math.random() * 1000));
        assignResponse.setStatus("ASSIGNED");

        // JSON olarak Kafka'ya gönder
        String assignResponseJson = objectMapper.writeValueAsString(assignResponse);
        kafkaTemplate.send(driverAssignedTopic, assignResponseJson);
        System.out.println("Driver Assigned Sent: " + assignResponse);
    }
}

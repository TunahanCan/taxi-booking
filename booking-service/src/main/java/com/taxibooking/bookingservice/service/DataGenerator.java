package com.taxibooking.bookingservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(100000)
@RequiredArgsConstructor
@Slf4j
public class DataGenerator {
    private final BookingKafkaService  bookingKafkaService;

    @PostConstruct
    public void init() {
        var tempValue = new BookingRequestDTO();
        tempValue.setBookingId(UUID.randomUUID().toString());
        tempValue.setBookingTime("12.12.2025");
        tempValue.setUserId("test-id");
        try {
            bookingKafkaService.sendBookingRequest(tempValue);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}

package com.taxibooking.bookingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingKafkaService {

    @PostConstruct
    public void init() {
        log.info("Booking service started and reduce random values");
    }

    private final KafkaTemplate<String, BookingRequestDTO> kafkaTemplateForBookingRequest;
    @Value("${booking.request.topic}")
    private String bookingRequestTopic;

    public void sendBookingRequest(BookingRequestDTO bookingRequest) throws JsonProcessingException {
        kafkaTemplateForBookingRequest.send(bookingRequestTopic, bookingRequest);
        log.warn("Booking Request Sent: {}", bookingRequest);
    }
}

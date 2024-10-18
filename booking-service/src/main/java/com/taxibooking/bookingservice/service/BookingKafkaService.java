package com.taxibooking.bookingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BookingKafkaService {
    @Value("${booking.request.topic}")
    private String bookingRequestTopic;
    private final KafkaTemplate<String, BookingRequestDTO> bookingKafkaTemplate;

    public BookingKafkaService(KafkaTemplate<String, BookingRequestDTO> bookingKafkaTemplate) {
        this.bookingKafkaTemplate = bookingKafkaTemplate;
    }

    public void sendBookingRequest(BookingRequestDTO bookingRequest) throws JsonProcessingException {
        bookingKafkaTemplate.send(bookingRequestTopic, bookingRequest);
    }
}

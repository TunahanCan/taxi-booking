package com.taxibooking.bookingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingCancelledDTO;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingKafkaService {

    @Value("${booking.request.topic}")
    private String bookingRequestTopic;

    @Value("${booking.cancelled.topic}")
    private String bookingCancelledTopic;

    private final KafkaTemplate<String, BookingRequestDTO> bookingRequestDTOKafkaTemplate;
    private final KafkaTemplate<String, BookingCancelledDTO> bookingCancelledKafkaTemplate;

    public void sendBookingRequest(BookingRequestDTO bookingRequest) throws JsonProcessingException {
        bookingRequestDTOKafkaTemplate.send(bookingRequestTopic, bookingRequest);
    }
    public void sendBookingCancelled(BookingCancelledDTO bookingCancelledDTO) throws JsonProcessingException {
        bookingCancelledKafkaTemplate.send(bookingCancelledTopic, bookingCancelledDTO);
    }
}

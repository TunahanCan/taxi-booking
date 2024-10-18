package com.taxibooking.bookingservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import com.taxibooking.bookingservice.service.BookingKafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/booking-service")
@RequiredArgsConstructor
public class BookingController {

    private final BookingKafkaService bookingKafkaService;

    @PostMapping("/book")
    public String createBooking(@RequestBody BookingRequestDTO bookingRequest) {
        try {
            bookingKafkaService.sendBookingRequest(bookingRequest);
            return "Booking request sent for bookingId: " + bookingRequest.getBookingId();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error in sending booking request.";
        }
    }
}

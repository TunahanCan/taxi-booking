package com.taxibooking.bookingservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingCancelledDTO;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import com.taxibooking.bookingservice.service.BookingKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/booking-service")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingKafkaService bookingKafkaService;
    @PostMapping("/booking-request")
    public String createBooking(@RequestBody BookingRequestDTO bookingRequest) {
        try {
            bookingKafkaService.sendBookingRequest(bookingRequest);
            return "Booking request sent for bookingId: " + bookingRequest.bookingId();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return "Error in sending booking request.";
        }
    }
    @PostMapping("/booking-cancelled")
    public String cancelBooking(@RequestBody BookingCancelledDTO bookingCancelledDTO) {
        try {
            bookingKafkaService.sendBookingCancelled(bookingCancelledDTO);
            return "Booking cancelled request.";
        } catch (JsonProcessingException e){
            log.error(e.getMessage());
            return "Error in sending booking cancelled";
        }
    }
}

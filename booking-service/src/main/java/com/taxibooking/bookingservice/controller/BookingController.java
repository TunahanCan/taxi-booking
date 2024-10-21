package com.taxibooking.bookingservice.controller;

import com.taxibooking.bookingservice.model.BookingCancelledDTO;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import com.taxibooking.bookingservice.service.BookingOrchestrationProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/booking-service")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingOrchestrationProducerService bookingOrchestrationProducerService;

    @PostMapping("/booking-request")
    public String createBooking(@RequestBody BookingRequestDTO bookingRequest) {
        bookingOrchestrationProducerService.sendBookingRequest(bookingRequest, "booking-request");
        return "Booking request sent for bookingId: " + bookingRequest.bookingId();
    }

    @PostMapping("/booking-cancelled")
    public String cancelBooking(@RequestBody BookingCancelledDTO bookingCancelledDTO) {
        bookingOrchestrationProducerService.sendBookingCancelled(bookingCancelledDTO, "booking-cancelled");
        return "Booking cancelled request.";
    }
}

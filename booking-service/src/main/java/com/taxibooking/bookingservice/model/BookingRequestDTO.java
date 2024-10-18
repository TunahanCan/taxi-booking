package com.taxibooking.bookingservice.model;

public record BookingRequestDTO(
        String bookingId,
        String customerName,
        String pickupLocation,
        String destination,
        Long amount
) { }

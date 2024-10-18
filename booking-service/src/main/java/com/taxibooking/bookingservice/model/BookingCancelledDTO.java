package com.taxibooking.bookingservice.model;


public record BookingCancelledDTO(
        String bookingId,
        String customerName,
        boolean booingStage) {
}

package com.taxibooking.bookingservice.model;

public record DriverStageDTO(
        String bookingId,
        String driverId,
        boolean driverAssigned,
        String failureReason) {
}

package com.taxibooking.bookingservice.model;


public record DriverDTO(
        String bookingId,
        String driverId,
        String driverName,
        boolean driverAssigned
) {}
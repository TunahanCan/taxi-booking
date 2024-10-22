package com.taxibooking.driverservice.model;


public record DriverTriggerDTO(
        String bookingId,
        String customerName,
        String pickupLocation,
        String dropoffLocation
) {}
package com.taxibooking.bookingservice.model;


public record DriverTriggerDTO(
        String bookingId,
        String pickupLocation,
        String dropoffLocation
) {}
package com.taxibooking.bookingservice.model;


import java.io.Serial;
import java.io.Serializable;

public record DriverTriggerDTO(
        String bookingId,
        String customerName,
        String pickupLocation,
        String dropoffLocation
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
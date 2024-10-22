package com.taxibooking.bookingservice.model;

import java.io.Serial;
import java.io.Serializable;

public record BookingRequestDTO(
        String bookingId,
        String customerName,
        String pickupLocation,
        String destination,
        Long amount
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

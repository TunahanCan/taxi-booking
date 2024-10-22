package com.taxibooking.bookingservice.model;

import java.io.Serial;
import java.io.Serializable;

public record PaymentStageDTO(
        String bookingId,
        String paymentId,
        boolean paymentCompleted,
        String paymentStatus,
        String failureReason) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}

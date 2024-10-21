package com.taxibooking.paymentservice.model;

public record PaymentStageDTO(
        String bookingId,
        String paymentId,
        boolean paymentCompleted,
        String paymentStatus,
        String failureReason) {
}

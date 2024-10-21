package com.taxibooking.paymentservice.model;


public record PaymentStageDTO(
        String bookingId,
        String customerName,
        double amount,
        String paymentStrategy,
        boolean paymentCompleted
) {}
package com.taxibooking.bookingservice.model;

public record PaymentDTO(
        String bookingId,
        String customerName,
        double amount,
        String paymentStrategy,
        boolean paymentCompleted
) {}
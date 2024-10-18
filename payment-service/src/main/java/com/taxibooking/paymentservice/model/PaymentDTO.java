package com.taxibooking.paymentservice.model;


public record PaymentDTO(
        String bookingId,
        String customerName,
        double amount,
        String paymentStrategy,
        boolean paymentCompleted
) {}
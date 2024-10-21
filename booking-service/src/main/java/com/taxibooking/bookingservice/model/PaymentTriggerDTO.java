package com.taxibooking.bookingservice.model;

public record PaymentTriggerDTO(
        String bookingId,
        String customerName,
        double amount,
        String paymentStrategy
) {}
package com.taxibooking.paymentservice.model;

public record PaymentTriggerDTO(
        String bookingId,
        String customerName,
        double amount,
        String paymentStrategy
) {}
package com.taxibooking.paymentservice.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentRequestDTO {
    private String bookingId;
    private String customerId;
    private double amount;
}
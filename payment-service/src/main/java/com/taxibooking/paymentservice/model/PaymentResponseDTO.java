package com.taxibooking.paymentservice.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponseDTO {
    private String bookingId;
    private String status;
    private String transactionId;
}

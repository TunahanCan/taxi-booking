package com.taxibooking.paymentservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.paymentservice.model.PaymentRequestDTO;
import com.taxibooking.paymentservice.model.PaymentResponseDTO;
import com.taxibooking.paymentservice.service.PaymentKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentKafkaService paymentKafkaService;

    @PostMapping("/processPayment")
    public String processPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        try {
            // Ödeme işlemi simülasyonu (Başarılı/başarısız)
            boolean paymentSuccessful = Math.random() > 0.5;

            // PaymentResponse oluştur
            PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
            paymentResponse.setBookingId(paymentRequest.getBookingId());
            paymentResponse.setTransactionId(UUID.randomUUID().toString());

            if (paymentSuccessful) {
                paymentResponse.setStatus("SUCCESS");
                paymentKafkaService.sendPaymentResponse(paymentResponse);
                return "Payment Processed Successfully for bookingId: " + paymentRequest.getBookingId();
            } else {
                paymentResponse.setStatus("FAILED");
                paymentKafkaService.sendPaymentResponse(paymentResponse);
                return "Payment Failed for bookingId: " + paymentRequest.getBookingId();
            }
        } catch (JsonProcessingException e) {
            log.error(e.toString());
            return "Error in processing payment.";
        }
    }

}

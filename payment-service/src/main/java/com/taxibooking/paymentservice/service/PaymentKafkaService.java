package com.taxibooking.paymentservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxibooking.paymentservice.model.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payment.success.topic}")
    private String paymentSuccessTopic;

    @Value("${payment.failed.topic}")
    private String paymentFailedTopic;

    public void sendPaymentResponse(PaymentResponseDTO paymentResponse) throws JsonProcessingException {
        String paymentResponseJson = objectMapper.writeValueAsString(paymentResponse);
        if ("SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
            kafkaTemplate.send(paymentSuccessTopic, paymentResponseJson);
            System.out.println("Payment Success Sent: " + paymentResponse);
        } else {
            kafkaTemplate.send(paymentFailedTopic, paymentResponseJson);
            System.out.println("Payment Failed Sent: " + paymentResponse);
        }
    }
}

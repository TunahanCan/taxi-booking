package com.taxibooking.paymentservice.service;

import com.taxibooking.paymentservice.model.PaymentStageDTO;
import com.taxibooking.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PaymentKafkaService {

    private final PaymentRepository paymentRepository;
    private final PaymentContextService paymentContextService;

    @Transactional
    @KafkaListener(topics = "payment-events", groupId = "payment-group")
    public void consumePaymentEvent(PaymentStageDTO payment) {
        System.out.println("Received payment event for booking: " + payment.bookingId());
        paymentContextService.processPayment(payment.paymentStrategy(),payment.amount());

    }
}

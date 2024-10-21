package com.taxibooking.paymentservice.service;


import com.taxibooking.paymentservice.model.PaymentStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducerService {

    @Value("${payment.stage.topic}")
    private String paymentStageTopic;

    private final KafkaTemplate<String, PaymentStageDTO> kafkaTemplateForPaymentStage ;

    public void sendPaymentStage(PaymentStageDTO paymentStage, String headerMessage) {
        Message<PaymentStageDTO> message = MessageBuilder
                .withPayload(paymentStage)
                .setHeader(KafkaHeaders.TOPIC, paymentStageTopic)
                .setHeader("payment-stage-message", headerMessage)
                .build();
        kafkaTemplateForPaymentStage.send(message);
    }
}

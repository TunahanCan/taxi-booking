package com.taxibooking.paymentservice.service;

import com.taxibooking.paymentservice.model.PaymentStageDTO;
import com.taxibooking.paymentservice.model.PaymentStageEntity;
import com.taxibooking.paymentservice.model.PaymentTriggerDTO;
import com.taxibooking.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaService {
    private final PaymentRepository paymentRepository;
    private final PaymentContextService paymentContextService;
    private final PaymentProducerService paymentProducerService;

    public static PaymentStageDTO convertToDTO(PaymentStageEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PaymentStageDTO(
                entity.getBookingId(),
                entity.getPaymentId(),
                entity.isPaymentCompleted(),
                entity.getPaymentStatus(),
                entity.getFailureReason()
        );
    }

    @KafkaListener(topics = "${payment.trigger.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "paymentTriggerKafkaListenerContainerFactory")
    public void handlePaymentEvent(PaymentTriggerDTO paymentTriggerDTO) {
        log.info("payment triggered request is received: {}", paymentTriggerDTO);
        PaymentStageEntity entity = new PaymentStageEntity();
        try {
            String paymentTransactionId =
                    paymentContextService.processPayment(paymentTriggerDTO.paymentStrategy(), paymentTriggerDTO.amount());
            entity.setPaymentTransactionId(paymentTransactionId);
            entity.setBookingId(paymentTriggerDTO.bookingId());
            entity.setPaymentDate(new Date());
            entity.setPaymentStatus("success");
            entity.setPaymentCompleted(true);
        } catch (IllegalArgumentException ie) {
            log.error(ie.getMessage());
            entity.setPaymentStatus("failed");
            entity.setPaymentCompleted(false);
            entity.setPaymentTransactionId(String.valueOf(ie.hashCode()));
        }
        paymentProducerService.sendPaymentStage(convertToDTO(entity) , "payment-stage-message");
        paymentRepository.save(entity);
    }
}

package com.taxibooking.bookingservice.service;

import com.taxibooking.bookingservice.model.BookingCancelledDTO;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import com.taxibooking.bookingservice.model.DriverTriggerDTO;
import com.taxibooking.bookingservice.model.PaymentTriggerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.Message;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingOrchestrationProducerService {

    private static final String BOOKING_REQUEST_MESSAGE_HEADER = "booking-request-message";
    private static final String BOOKING_CANCELLED_MESSAGE_HEADER = "booking-cancelled-message";
    private static final String PAYMENT_TRIGGER_MESSAGE_HEADER = "payment-trigger-message";
    private static final String DRIVER_TRIGGER_MESSAGE_HEADER = "driver-trigger-message";

    private final KafkaTemplate<String, BookingRequestDTO> bookingRequestKafkaTemplate;
    private final KafkaTemplate<String, BookingCancelledDTO> bookingCancelledKafkaTemplate;
    private final KafkaTemplate<String, PaymentTriggerDTO> paymentTriggerKafkaTemplate;
    private final KafkaTemplate<String, DriverTriggerDTO> driverTriggerKafkaTemplate;

    @Value("${booking.request.topic}")
    private String bookingRequestTopic;

    @Value("${booking.cancelled.topic}")
    private String bookingCancelledTopic;

    @Value("${payment.trigger.topic}")
    private String paymentTriggerTopic;

    @Value("${driver.trigger.topic}")
    private String driverTriggerTopic;


    public void sendBookingRequest(BookingRequestDTO bookingRequest, String headerMessage) {
        sendMessage(bookingRequestKafkaTemplate, bookingRequest, bookingRequestTopic, BOOKING_REQUEST_MESSAGE_HEADER, headerMessage);
    }

    public void sendBookingCancelled(BookingCancelledDTO bookingCancelledDTO, String headerMessage) {
        sendMessage(bookingCancelledKafkaTemplate, bookingCancelledDTO, bookingCancelledTopic, BOOKING_CANCELLED_MESSAGE_HEADER, headerMessage);
    }

    public void sendPaymentTrigger(PaymentTriggerDTO paymentTriggerDTO, String headerMessage) {
        sendMessage(paymentTriggerKafkaTemplate, paymentTriggerDTO, paymentTriggerTopic, PAYMENT_TRIGGER_MESSAGE_HEADER, headerMessage);
    }

    public void sendDriverTrigger(DriverTriggerDTO driverTriggerDTO, String headerMessage) {
        sendMessage(driverTriggerKafkaTemplate, driverTriggerDTO, driverTriggerTopic, DRIVER_TRIGGER_MESSAGE_HEADER, headerMessage);
    }

    private <T> void sendMessage(KafkaTemplate<String, T> kafkaTemplate, T payload, String topic, String headerKey, String headerMessage) {
        try {
            Message<T> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(headerKey, headerMessage)
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Message successfully sent to topic {}: {}, offset: {}",
                                    topic, payload, result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send message to topic {}: {}", topic, ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error while sending message to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Message sending failed", e);
        }
    }
}
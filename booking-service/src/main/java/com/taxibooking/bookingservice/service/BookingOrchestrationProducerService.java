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

    @Value("${booking.request.topic}")
    private String bookingRequestTopic;

    @Value("${booking.cancelled.topic}")
    private String bookingCancelledTopic;

    @Value("${payment.trigger.topic}")
    private String paymentTriggerTopic;

    @Value("${driver.trigger.topic}")
    private String driverTriggerTopic;

    private final KafkaTemplate<String, BookingRequestDTO> bookingRequestKafkaTemplate;
    private final KafkaTemplate<String, BookingCancelledDTO> bookingCancelledKafkaTemplate;
    private final KafkaTemplate<String, PaymentTriggerDTO> paymentTriggerKafkaTemplate;
    private final KafkaTemplate<String, DriverTriggerDTO> driverTriggerKafkaTemplate;

    public void sendBookingRequest(BookingRequestDTO bookingRequest, String headerMessage) {
        Message<BookingRequestDTO> message = MessageBuilder
                .withPayload(bookingRequest)
                .setHeader(KafkaHeaders.TOPIC, bookingRequestTopic)
                .setHeader("booking-request-message", headerMessage)
                .build();
        bookingRequestKafkaTemplate.send(message);
    }
    public void sendBookingCancelled(BookingCancelledDTO bookingCancelledDTO , String headerMessage) {
        Message<BookingCancelledDTO> message = MessageBuilder
                .withPayload(bookingCancelledDTO)
                .setHeader(KafkaHeaders.TOPIC, bookingCancelledTopic)
                .setHeader("booking-cancelled-message", headerMessage)
                .build();
        bookingCancelledKafkaTemplate.send(message);
    }
    public void sendPaymentTrigger(PaymentTriggerDTO paymentTriggerDTO, String headerMessage) {
        Message<PaymentTriggerDTO> message = MessageBuilder
                .withPayload(paymentTriggerDTO)
                .setHeader(KafkaHeaders.TOPIC, paymentTriggerTopic)
                .setHeader("payment-trigger-message", headerMessage)
                .build();
        paymentTriggerKafkaTemplate.send(message);
    }
    public void sendDriverTrigger(DriverTriggerDTO driverTriggerDTO, String headerMessage) {
        Message<DriverTriggerDTO> message = MessageBuilder
                .withPayload(driverTriggerDTO)
                .setHeader(KafkaHeaders.TOPIC, driverTriggerTopic)
                .setHeader("driver-trigger-message", headerMessage)
                .build();
        driverTriggerKafkaTemplate.send(message);
    }
}

package com.taxibooking.bookingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    private final KafkaTemplate<String, BookingRequestDTO> bookingRequestDTOKafkaTemplate;
    private final KafkaTemplate<String, BookingCancelledDTO> bookingCancelledKafkaTemplate;
    private final KafkaTemplate<String, PaymentTriggerDTO> paymentTriggerKafkaTemplate;
    private final KafkaTemplate<String, DriverTriggerDTO> driverTriggerKafkaTemplate;

    public void sendBookingRequest(BookingRequestDTO bookingRequest) {
        bookingRequestDTOKafkaTemplate.send(bookingRequestTopic, bookingRequest);
    }
    public void sendBookingCancelled(BookingCancelledDTO bookingCancelledDTO) {
        bookingCancelledKafkaTemplate.send(bookingCancelledTopic, bookingCancelledDTO);
    }
    public void sendPaymentTrigger(PaymentTriggerDTO paymentTriggerDTO, String headerMessage) {
        Message<PaymentTriggerDTO> message = MessageBuilder
                .withPayload(paymentTriggerDTO)
                .setHeader(KafkaHeaders.TOPIC, driverTriggerTopic)
                .setHeader("payment-message", headerMessage) // Özel bir header ekliyoruz
                .build();
        paymentTriggerKafkaTemplate.send(paymentTriggerTopic, paymentTriggerDTO);
    }
    public void sendDriverTrigger(DriverTriggerDTO driverTriggerDTO, String headerMessage) {
        Message<DriverTriggerDTO> message = MessageBuilder
                .withPayload(driverTriggerDTO)
                .setHeader(KafkaHeaders.TOPIC, driverTriggerTopic)
                .setHeader("driver-message", headerMessage) // Özel bir header ekliyoruz
                .build();
        driverTriggerKafkaTemplate.send(message);
    }
}

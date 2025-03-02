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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingOrchestrationProducerService {

    private static final String BOOKING_REQUEST_MESSAGE_HEADER = "booking-request-message";
    private static final String BOOKING_CANCELLED_MESSAGE_HEADER = "booking-cancelled-message";
    private static final String PAYMENT_TRIGGER_MESSAGE_HEADER = "payment-trigger-message";
    private static final String DRIVER_TRIGGER_MESSAGE_HEADER = "driver-trigger-message";

    @Value("${booking.request.topic}")
    private String bookingRequestTopic;

    @Value("${booking.cancelled.topic}")
    private String bookingCancelledTopic;

    @Value("${payment.trigger.topic}")
    private String paymentTriggerTopic;

    @Value("${driver.trigger.topic}")
    private String driverTriggerTopic;

    @Value("${kafka.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${kafka.retry.backoff-ms:1000}")
    private long retryBackoffMs;

    private final KafkaTemplate<String, BookingRequestDTO> bookingRequestKafkaTemplate;
    private final KafkaTemplate<String, BookingCancelledDTO> bookingCancelledKafkaTemplate;
    private final KafkaTemplate<String, PaymentTriggerDTO> paymentTriggerKafkaTemplate;
    private final KafkaTemplate<String, DriverTriggerDTO> driverTriggerKafkaTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

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
            validateMessage(payload);
            Message<T> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(headerKey, headerMessage)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, getMessageKey(payload))
                    .build();
            
            kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message successfully sent to topic {}: {}, offset: {}", 
                            topic, payload, result.getRecordMetadata().offset());
                        recordMetrics(topic, true);
                    } else {
                        log.error("Failed to send message to topic {}: {}", topic, ex.getMessage());
                        recordMetrics(topic, false);
                    }
                });
        } catch (Exception e) {
            log.error("Error while sending message to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Message sending failed", e);
        }
    }

    private <T> void sendMessageWithRetry(KafkaTemplate<String, T> kafkaTemplate, T payload, String topic, String headerKey, String headerMessage) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxRetryAttempts) {
            try {
                sendMessage(kafkaTemplate, payload, topic, headerKey, headerMessage);
                return;
            } catch (Exception e) {
                lastException = e;
                attempts++;
                log.warn("Attempt {} failed for topic {}: {}", attempts, topic, e.getMessage());
                
                if (attempts < maxRetryAttempts) {
                    try {
                        Thread.sleep(retryBackoffMs * attempts); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetryAttempts + " attempts", lastException);
    }

    private <T> void sendMessageWithCircuitBreaker(KafkaTemplate<String, T> kafkaTemplate, T payload, String topic, String headerKey, String headerMessage) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("kafka-publisher");
        
        circuitBreaker.run(
            () -> sendMessageWithRetry(kafkaTemplate, payload, topic, headerKey, headerMessage),
            throwable -> handleCircuitBreakerFailure(throwable, topic)
        );
    }

    private String getMessageKey(Object payload) {
        if (payload instanceof BookingRequestDTO) {
            return ((BookingRequestDTO) payload).getBookingId();
        } else if (payload instanceof BookingCancelledDTO) {
            return ((BookingCancelledDTO) payload).getBookingId();
        }
        return UUID.randomUUID().toString();
    }

    public CompletableFuture<SendResult<String, BookingRequestDTO>> sendBookingRequestAsync(BookingRequestDTO bookingRequest, String headerMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Message<BookingRequestDTO> message = MessageBuilder
                        .withPayload(bookingRequest)
                        .setHeader(KafkaHeaders.TOPIC, bookingRequestTopic)
                        .setHeader(BOOKING_REQUEST_MESSAGE_HEADER, headerMessage)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, getMessageKey(bookingRequest))
                        .build();
                        
                return bookingRequestKafkaTemplate.send(message).get();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    private <T> void validateMessage(T payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        
        if (payload instanceof BookingRequestDTO) {
            BookingRequestDTO booking = (BookingRequestDTO) payload;
            if (booking.bookingId() == null || booking.bookingId().trim().isEmpty()) {
                throw new IllegalArgumentException("BookingId cannot be null or empty");
            }
        }
        // Diğer DTO'lar için benzer validasyonlar
    }

    private void recordMetrics(String topic, boolean success) {
        Counter.builder("kafka.messages")
                .tag("topic", topic)
                .tag("status", success ? "success" : "failure")
                .register(meterRegistry)
                .increment();
    }

    private void handleCircuitBreakerFailure(Throwable throwable, String topic) {
        log.error("Circuit breaker triggered for topic {}: {}", topic, throwable.getMessage());
        recordMetrics(topic, false);
    }
}
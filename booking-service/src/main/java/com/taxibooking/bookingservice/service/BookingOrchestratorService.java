package com.taxibooking.bookingservice.service;

import com.taxibooking.bookingservice.enums.PaymentEnum;
import com.taxibooking.bookingservice.model.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BookingOrchestratorService {

    private final BookingOrchestrationProducerService bookingOrchestrationProducerService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Random random = new Random();

    public BookingOrchestratorService(BookingOrchestrationProducerService bookingOrchestrationProducerService,
                                      RedisTemplate<String, Object> redisTemplate) {
        this.bookingOrchestrationProducerService = bookingOrchestrationProducerService;
        this.redisTemplate = redisTemplate;
    }

    private PaymentEnum getRandomPaymentEnum() {
        PaymentEnum[] paymentEnums = PaymentEnum.values();
        return paymentEnums[random.nextInt(paymentEnums.length)];
    }


    public Map<String, Object> getBookingDetails(String bookingId) {
        Map<Object, Object> bookingDetails = redisTemplate.opsForHash().entries(bookingId);
        return bookingDetails.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> (String) e.getKey(), Map.Entry::getValue));
    }

    public void updatePaymentDetails(String bookingId, PaymentStageDTO PaymentStageDTO) {
        redisTemplate.opsForHash().put(bookingId, "payment", PaymentStageDTO);
    }

    public void removeBookingDetails(String bookingId) {
        redisTemplate.delete(bookingId);
    }

    /**
     * @param bookingRequest
     * @implNote this listener process the reletad booking request topic
     */
    @KafkaListener(topics = "${booking.request.topic}", groupId = "booking-group",
            containerFactory = "bookingKafkaListenerContainerFactory")
    public void handleBookingRequestEvent(BookingRequestDTO bookingRequest) {
        System.out.println("Processing booking: " + bookingRequest.bookingId());
        PaymentTriggerDTO paymentTriggerDTO = new PaymentTriggerDTO(
                bookingRequest.bookingId(),
                bookingRequest.customerName(),
                bookingRequest.amount(),
                getRandomPaymentEnum().name()
        );
        redisTemplate.opsForHash().put(bookingRequest.bookingId(), "booking-request", bookingRequest);
        bookingOrchestrationProducerService.sendPaymentTrigger(paymentTriggerDTO,"payment-events");
    }


    @KafkaListener(topics = "${booking.cancelled.topic}", groupId = "booking-group",
            containerFactory = "bookingKafkaListenerContainerFactory")
    public void handleBookingCancelledEvent(BookingCancelledDTO bookingCancelledDTO) {
        redisTemplate.opsForHash().put(bookingCancelledDTO.bookingId(), "booking-cancelled", bookingCancelledDTO);
    }


    @KafkaListener(topics = "payment-events", groupId = "booking-group",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void handlePaymentEvent(PaymentStageDTO PaymentStageDTO) {
        if (PaymentStageDTO.paymentCompleted()) {
            DriverTriggerDTO driverTriggerDTO = new DriverTriggerDTO(
                    PaymentStageDTO.bookingId(),
                    "driver123",
                    "John Doe"
            );
            redisTemplate.opsForHash().put(PaymentStageDTO.bookingId(), "payment-success", PaymentStageDTO);
            bookingOrchestrationProducerService.sendDriverTrigger(driverTriggerDTO,"driver-events");
        } else {
            BookingCancelledDTO bookingCancelledDTO =
                    new BookingCancelledDTO(PaymentStageDTO.bookingId(),
                            "test-name", false);
            bookingOrchestrationProducerService.sendBookingCancelled(bookingCancelledDTO);
        }
    }

    @KafkaListener(topics = "driver-events", groupId = "booking-group",
            containerFactory = "driverKafkaListenerContainerFactory")
    public void handleDriverEvent(DriverStageDTO driverTriggerDTO) {
        if (driverTriggerDTO.driverAssigned()) {
            System.out.println("Booking completed successfully for: " + driverTriggerDTO.bookingId());
            redisTemplate.opsForHash().put(driverTriggerDTO.bookingId(), "driver-assigned", driverTriggerDTO);
        } else {
            System.out.println("Driver assignment failed for booking: " + driverTriggerDTO.bookingId());
        }
    }

    // Geri alma (rollback) işlemini yönetir
    private void rollbackTransaction(String bookingId, String reason) {
        System.out.println("Rolling back transaction for booking: " + bookingId + " due to: " + reason);
        BookingRequestDTO bookingRequest = (BookingRequestDTO) redisTemplate.opsForList().leftPop(bookingId);
        if (bookingRequest != null) {
            // Kafka üzerinden rezervasyon iptali mesajı gönder
            BookingCancelledDTO bookingCancelledDTO = new BookingCancelledDTO(
                    bookingId,
                    bookingRequest.customerName(),
                    false // İptal durumu
            );
            redisTemplate.delete(bookingId);
        }
    }


}

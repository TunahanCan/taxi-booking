package com.taxibooking.bookingservice.service;

import com.taxibooking.bookingservice.enums.PaymentEnum;
import com.taxibooking.bookingservice.model.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Random;

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

    /**
     * @param bookingRequest
     * @implNote this listener process the reletad booking request topic
     */
    @KafkaListener(topics = "${booking.request.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "bookingRequestKafkaListenerContainerFactory")
    public void handleBookingRequestEvent(BookingRequestDTO bookingRequest) {
        System.out.println("Processing booking: " + bookingRequest.bookingId());
        PaymentTriggerDTO paymentTriggerDTO = new PaymentTriggerDTO(
                bookingRequest.bookingId(),
                bookingRequest.customerName(),
                bookingRequest.amount(),
                getRandomPaymentEnum().name()
        );
        redisTemplate.opsForHash().put(bookingRequest.bookingId(), "booking-request", bookingRequest);
        bookingOrchestrationProducerService.sendPaymentTrigger(paymentTriggerDTO, "payment-events");
    }

    public BookingRequestDTO getBookingRequestById(String bookingId) {
        Object bookingRequest = redisTemplate.opsForHash().get(bookingId, "booking-request");
        if (bookingRequest instanceof BookingRequestDTO) {
            return (BookingRequestDTO) bookingRequest;
        }
        return null;
    }

    /**
     * will be implemented rollback scenario
     *
     * @param bookingCancelledDTO
     */
    @KafkaListener(topics = "${booking.cancelled.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "bookingCancelledKafkaListenerContainerFactory")
    public void handleBookingCancelledEvent(BookingCancelledDTO bookingCancelledDTO) {
        redisTemplate.opsForHash().put(bookingCancelledDTO.bookingId(), "booking-cancelled", bookingCancelledDTO);
    }


    @KafkaListener(topics = "${payment.stage.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void handlePaymentEvent(PaymentStageDTO paymentStageDTO) {
        if (paymentStageDTO.paymentCompleted()) {
            BookingRequestDTO requestDTO = getBookingRequestById(paymentStageDTO.bookingId());
            if (requestDTO == null) {
                rollbackTransaction(paymentStageDTO.bookingId(), "booking-can-not-be-found");
                throw new IllegalStateException("booking-can-not-be-found");
            }
            DriverTriggerDTO driverTriggerDTO = new DriverTriggerDTO(
                    paymentStageDTO.bookingId(),
                    requestDTO.customerName(),
                    requestDTO.pickupLocation(),
                    requestDTO.destination()
            );
            redisTemplate.opsForHash().put(paymentStageDTO.bookingId(), "payment-success", paymentStageDTO);
            bookingOrchestrationProducerService.sendDriverTrigger(driverTriggerDTO, "driver-events");
        } else {
            BookingCancelledDTO bookingCancelledDTO =
                    new BookingCancelledDTO(paymentStageDTO.bookingId(),
                            "test-name", false);
            bookingOrchestrationProducerService.sendBookingCancelled(bookingCancelledDTO, "booking-cancelled");
        }
    }

    @KafkaListener(topics = "driver-events", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "driverKafkaListenerContainerFactory")
    public void handleDriverEvent(DriverStageDTO driverStageDTO) {
        if (driverStageDTO.driverAssigned()) {
            System.out.println("Booking completed successfully for: " + driverStageDTO.bookingId());
            redisTemplate.opsForHash().put(driverStageDTO.bookingId(), "driver-assigned", driverStageDTO);
        } else {
            rollbackTransaction(driverStageDTO.bookingId(), "driver-is-not-assigned");
            System.out.println("Driver assignment failed for booking: " + driverStageDTO.bookingId());
        }
    }

    private void rollbackTransaction(String bookingId, String reason) {
        System.out.println("Rolling back transaction for booking: " + bookingId + " due to: " + reason);
        BookingRequestDTO bookingRequest = (BookingRequestDTO) redisTemplate.opsForList().leftPop(bookingId);
        if (bookingRequest != null) {
            BookingCancelledDTO bookingCancelledDTO = new BookingCancelledDTO(
                    bookingId,
                    bookingRequest.customerName(),
                    false
            );
            redisTemplate.delete(bookingId);
        }
    }
}

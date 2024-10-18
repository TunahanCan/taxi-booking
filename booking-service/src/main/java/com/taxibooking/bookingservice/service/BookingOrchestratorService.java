package com.taxibooking.bookingservice.service;


import com.taxibooking.bookingservice.model.BookingRequestDTO;
import com.taxibooking.bookingservice.model.DriverDTO;
import com.taxibooking.bookingservice.model.PaymentDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingOrchestratorService {
    private final KafkaTemplate<String, PaymentDTO> paymentKafkaTemplate;
    private final KafkaTemplate<String, DriverDTO> driverKafkaTemplate;
    public BookingOrchestratorService(KafkaTemplate<String, PaymentDTO> paymentKafkaTemplate,
                                      KafkaTemplate<String, DriverDTO> driverKafkaTemplate) {
        this.paymentKafkaTemplate = paymentKafkaTemplate;
        this.driverKafkaTemplate = driverKafkaTemplate;
    }
    @KafkaListener(topics = "booking-events", groupId = "booking-group", containerFactory = "bookingKafkaListenerContainerFactory")
    public void handleBookingEvent(BookingRequestDTO bookingRequest) {
        System.out.println("Processing booking: " + bookingRequest.bookingId());
        PaymentDTO paymentDTO = new PaymentDTO(
                bookingRequest.bookingId(),
                bookingRequest.customerName(),
                100.0, // Sabit bir ücret örneği
                false
        );
        paymentKafkaTemplate.send("payment-events", paymentDTO);
    }

    @KafkaListener(topics = "payment-events", groupId = "booking-group",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void handlePaymentEvent(PaymentDTO paymentDTO) {
        if (paymentDTO.paymentCompleted()) {
            DriverDTO driverDTO = new DriverDTO(
                    paymentDTO.bookingId(),
                    "driver123",
                    "John Doe",
                    true
            );
            driverKafkaTemplate.send("driver-events", driverDTO);
        } else {
            System.out.println("Payment failed for booking: " + paymentDTO.bookingId());
        }
    }

    @KafkaListener(topics = "driver-events", groupId = "booking-group",
            containerFactory = "driverKafkaListenerContainerFactory")
    public void handleDriverEvent(DriverDTO driverDTO) {
        if (driverDTO.driverAssigned()) {
            System.out.println("Booking completed successfully for: " + driverDTO.bookingId());
        } else {
            System.out.println("Driver assignment failed for booking: " + driverDTO.bookingId());
        }
    }
}

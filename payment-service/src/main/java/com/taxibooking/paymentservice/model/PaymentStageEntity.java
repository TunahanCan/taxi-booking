package com.taxibooking.paymentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class PaymentStageEntity {
    @Id
    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;
    @Column(name = "booking_id", nullable = false)
    private String bookingId;
    @Column(name = "payment_completed", nullable = false)
    private boolean paymentCompleted;
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;
    @Column(name = "failure_reason")
    private String failureReason;
    @Column(name = "payment_transaction_id" , nullable = false)
    private String paymentTransactionId;
    @Column
    private Date paymentDate;
}

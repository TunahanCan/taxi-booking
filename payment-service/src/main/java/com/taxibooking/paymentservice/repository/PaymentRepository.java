package com.taxibooking.paymentservice.repository;


import com.taxibooking.paymentservice.model.PaymentStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentStageEntity, Long> {
}

package com.taxibooking.paymentservice.service;

import com.taxibooking.paymentservice.model.Payment;
import com.taxibooking.paymentservice.repository.PaymentRepository;
import com.taxibooking.paymentservice.strategy.BitcoinPayment;
import com.taxibooking.paymentservice.strategy.CashPayment;
import com.taxibooking.paymentservice.strategy.CreditPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentKafkaService {

}

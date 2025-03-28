package com.taxibooking.paymentservice.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BitcoinPayment implements PaymentStrategy {
    @Override
    public String performPay(double amount) {
        log.info("Paying {} bitcoin payment", amount);
        return UUID.randomUUID().toString();
    }
}

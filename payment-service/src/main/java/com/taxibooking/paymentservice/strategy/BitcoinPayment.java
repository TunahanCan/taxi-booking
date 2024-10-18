package com.taxibooking.paymentservice.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BitcoinPayment implements PaymentStrategy {

    @Override
    public void performPay(double amount) {
        log.info("Paying {} bitcoin payment", amount);
    }
}

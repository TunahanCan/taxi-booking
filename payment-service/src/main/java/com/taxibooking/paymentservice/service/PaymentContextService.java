package com.taxibooking.paymentservice.service;


import com.taxibooking.paymentservice.enums.PaymentEnum;
import com.taxibooking.paymentservice.strategy.BitcoinPayment;
import com.taxibooking.paymentservice.strategy.CashPayment;
import com.taxibooking.paymentservice.strategy.CreditPayment;
import com.taxibooking.paymentservice.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentContextService {

    private final BitcoinPayment bitcoinPayment;
    private final CreditPayment creditCardPayment;
    private final CashPayment cashPayment;

    public void processPayment(String paymentType, double amount) {
        PaymentEnum paymentMethod;
        try {
            paymentMethod = PaymentEnum.valueOf(paymentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown payment method: " + paymentType);
        }
        PaymentStrategy strategy = switch (paymentMethod) {
            case BITCOIN -> bitcoinPayment;
            case CREDIT_CARD -> creditCardPayment;
            case CASH_PAYMENT -> cashPayment;
            default -> throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        };
        strategy.performPay(amount);
    }
}

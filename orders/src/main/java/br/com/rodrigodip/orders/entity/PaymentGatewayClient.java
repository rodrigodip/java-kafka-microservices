package br.com.rodrigodip.orders.entity;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentGatewayClient {

    public String processPayment(Order order) {
        log.info("Requesting Payment to Order_id:{}", order.getId());
        return UUID.randomUUID().toString();
    }
}

package br.com.rodrigodip.orders.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.orders.client.dto.PaymentGateway;
import br.com.rodrigodip.orders.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final PaymentGatewayService paymentService;

    @PostMapping("/payment-callback")
    public ResponseEntity<Object> processPayment(
            @RequestBody PaymentGateway body,
            @RequestHeader(required = true, name = "apiKey") String apikey) {

        paymentService.updatePaymentStatus(body);

        return ResponseEntity.ok().build();
    }
}

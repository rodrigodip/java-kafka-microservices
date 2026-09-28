package br.com.rodrigodip.orders.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.client.dto.PaymentGateway;
import br.com.rodrigodip.orders.exceptions.PaymentCallbackUnauthorizedException;
import br.com.rodrigodip.orders.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final PaymentGatewayService paymentService;
    private final PaymentProperties paymentProperties;

    @PostMapping("/payment-callback")
    public ResponseEntity<Object> processPayment(
            @RequestBody PaymentGateway body,
            @RequestHeader(required = true, name = "apiKey") String apikey) {

        if (!isValidCallbackKey(apikey)) {
            throw new PaymentCallbackUnauthorizedException();
        }

        paymentService.updatePaymentStatus(body);

        return ResponseEntity.ok().build();
    }

    private boolean isValidCallbackKey(String apikey) {
        String expected = paymentProperties.getCallbackApiKey();
        if (expected == null || apikey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                apikey.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }
}

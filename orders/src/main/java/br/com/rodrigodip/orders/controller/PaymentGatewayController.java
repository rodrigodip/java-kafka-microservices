package br.com.rodrigodip.orders.controller;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.client.dto.PaymentGatewayResponse;
import br.com.rodrigodip.orders.exceptions.InvalidCallbackPayloadException;
import br.com.rodrigodip.orders.exceptions.PaymentCallbackUnauthorizedException;
import br.com.rodrigodip.orders.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private static final String SIGNATURE_HEADER = "X-Signature";

    private final PaymentGatewayService paymentService;
    private final PaymentProperties paymentProperties;
    private final ObjectMapper objectMapper;

    @PostMapping("/payment-callback")
    public ResponseEntity<Object> processPayment(
            @RequestBody String rawBody,
            @RequestHeader(required = true, name = SIGNATURE_HEADER) String signature) {

        if (!isValidSignature(rawBody, signature)) {
            throw new PaymentCallbackUnauthorizedException();
        }

        paymentService.updatePaymentStatus(parseBody(rawBody));

        return ResponseEntity.ok().build();
    }

    private boolean isValidSignature(String rawBody, String signature) {
        String secret = paymentProperties.getCallbackApiKey();
        if (secret == null || signature == null) {
            return false;
        }

        String expected = hmacSha256Hex(rawBody, secret);
        return MessageDigest.isEqual(
                signature.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256Hex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to compute HMAC-SHA256 signature", e);
        }
    }

    private PaymentGatewayResponse parseBody(String rawBody) {
        try {
            return objectMapper.readValue(rawBody, PaymentGatewayResponse.class);
        } catch (JacksonException e) {
            throw new InvalidCallbackPayloadException("Malformed PSP callback body", e);
        }
    }
}

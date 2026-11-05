package br.com.rodrigodip.orders.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.client.dto.PaymentGatewayResponse;
import br.com.rodrigodip.orders.exceptions.PaymentCallbackUnauthorizedException;
import br.com.rodrigodip.orders.service.PaymentGatewayService;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayControllerTest {

    private static final String SECRET = "test-secret";

    @Mock
    private PaymentGatewayService paymentGatewayService;

    @Mock
    private PaymentProperties paymentProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentGatewayController paymentGatewayController;

    @Test
    void acceptsCallbackWithValidHmacSignature() throws Exception {
        String body = "{\"id\":1,\"paymentKey\":\"key-1\",\"status\":true,\"notes\":\"ok\"}";
        String signature = hmacSha256Hex(body, SECRET);
        when(paymentProperties.getCallbackApiKey()).thenReturn(SECRET);
        when(objectMapper.readValue(body, PaymentGatewayResponse.class))
                .thenReturn(new PaymentGatewayResponse(1L, "key-1", true, "ok"));

        ResponseEntity<Object> response = paymentGatewayController.processPayment(body, signature);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(paymentGatewayService).updatePaymentStatus(any(PaymentGatewayResponse.class));
    }

    @Test
    void rejectsCallbackWithInvalidHmacSignature() {
        String body = "{\"id\":1,\"paymentKey\":\"key-1\",\"status\":true,\"notes\":\"ok\"}";
        when(paymentProperties.getCallbackApiKey()).thenReturn(SECRET);

        assertThatThrownBy(() -> paymentGatewayController.processPayment(body, "invalid-signature"))
                .isInstanceOf(PaymentCallbackUnauthorizedException.class);

        verify(paymentGatewayService, never()).updatePaymentStatus(any());
    }

    @Test
    void rejectsCallbackWhenSecretIsMissing() {
        String body = "{\"id\":1,\"paymentKey\":\"key-1\",\"status\":true,\"notes\":\"ok\"}";
        when(paymentProperties.getCallbackApiKey()).thenReturn(null);

        assertThatThrownBy(() -> paymentGatewayController.processPayment(body, "any-signature"))
                .isInstanceOf(PaymentCallbackUnauthorizedException.class);

        verify(paymentGatewayService, never()).updatePaymentStatus(any());
    }

    private String hmacSha256Hex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to compute HMAC", e);
        }
    }
}

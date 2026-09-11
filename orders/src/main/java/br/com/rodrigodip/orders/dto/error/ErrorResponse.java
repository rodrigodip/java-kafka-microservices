package br.com.rodrigodip.orders.dto.error;

import java.time.Instant;

public record ErrorResponse(
                Instant timestamp,
                Integer status,
                String error,
                String message,
                String path) {
}
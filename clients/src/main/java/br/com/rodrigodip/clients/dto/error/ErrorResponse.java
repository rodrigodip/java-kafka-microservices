package br.com.rodrigodip.clients.dto.error;

import java.time.Instant;

public record ErrorResponse(
                Instant timestamp,
                Integer status,
                String error,
                String message,
                String path) {
}
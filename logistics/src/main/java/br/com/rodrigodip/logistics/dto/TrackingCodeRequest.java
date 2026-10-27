package br.com.rodrigodip.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrackingCodeRequest(
        @NotBlank(message = "trackingCode is required")
        @Size(max = 255, message = "trackingCode must have at most 255 characters")
        String trackingCode) {
}

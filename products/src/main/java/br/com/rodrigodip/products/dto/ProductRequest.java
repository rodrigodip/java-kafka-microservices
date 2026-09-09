package br.com.rodrigodip.products.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(

		@NotBlank @Size(max = 10) String name,

		@NotNull @Digits(integer = 9, fraction = 2) @DecimalMin(value = "0.0", inclusive = false) BigDecimal price) {
}

package br.com.rodrigodip.clients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(

		@NotBlank @Size(max = 150) String name,
		@NotBlank @Size(max = 11) String cpf,
		@NotBlank @Email @Size(max = 100) String email,
		@Size(max = 20) String phone,
		@Size(max = 100) String address,
		@Size(max = 10) String number,
		@Size(max = 8) String zipcode) {
}

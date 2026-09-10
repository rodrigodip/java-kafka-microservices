package br.com.rodrigodip.clients.dto;

public record ClientResponse(
        Long Id,
        String name,
        String cpf,
        String email,
        String phone,
        String address,
        String number,
        String zipcode) {
}
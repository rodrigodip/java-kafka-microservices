package br.com.rodrigodip.orders.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import br.com.rodrigodip.orders.client.dto.ClientResponse;

@HttpExchange("/clients")
public interface ClientServiceClient {

    @GetExchange("/{id}")
    ClientResponse findById(@PathVariable("id") Long id);
}

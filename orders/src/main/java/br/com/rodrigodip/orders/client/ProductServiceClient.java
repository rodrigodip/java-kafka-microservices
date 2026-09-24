package br.com.rodrigodip.orders.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import br.com.rodrigodip.orders.client.dto.ProductResponse;

@HttpExchange("/products")
public interface ProductServiceClient {

    @GetExchange("/{id}")
    ProductResponse findById(@PathVariable("id") Long id);
}

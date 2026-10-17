package br.com.rodrigodip.orders.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import br.com.rodrigodip.orders.client.dto.ClientResponse;
import br.com.rodrigodip.orders.client.dto.ProductResponse;
import br.com.rodrigodip.orders.exceptions.ClientNotFoundOnClientException;
import br.com.rodrigodip.orders.exceptions.DownstreamUnavailableException;
import br.com.rodrigodip.orders.exceptions.ProductNotFoundOnClientException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderDataProvider {

    private final ClientServiceClient clientServiceClient;
    private final ProductServiceClient productServiceClient;

    public ClientResponse findClient(Long clientId) {
        try {
            return clientServiceClient.findById(clientId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ClientNotFoundOnClientException(clientId);
        } catch (RestClientException e) {
            throw new DownstreamUnavailableException("clients", e);
        }
    }

    public ProductResponse findProduct(Long productId) {
        try {
            return productServiceClient.findById(productId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundOnClientException(productId);
        } catch (RestClientException e) {
            throw new DownstreamUnavailableException("products", e);
        }
    }
}

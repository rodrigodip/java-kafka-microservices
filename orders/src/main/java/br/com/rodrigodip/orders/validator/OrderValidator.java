package br.com.rodrigodip.orders.validator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import br.com.rodrigodip.orders.client.ClientServiceClient;
import br.com.rodrigodip.orders.client.ProductServiceClient;
import br.com.rodrigodip.orders.client.dto.ClientResponse;
import br.com.rodrigodip.orders.client.dto.ProductResponse;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.entity.OrderItem;
import br.com.rodrigodip.orders.exceptions.ClientNotFoundOnClientException;
import br.com.rodrigodip.orders.exceptions.DownstreamUnavailableException;
import br.com.rodrigodip.orders.exceptions.ProductNotFoundOnClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValidator {

    private final ClientServiceClient clientServiceClient;
    private final ProductServiceClient productServiceClient;

    public void validate(Order order) {
        ClientResponse client = fetchClient(order.getClientId());
        validateClient(order, client);

        List<ProductResponse> products = order.getItensList().stream()
                .map(item -> fetchProduct(item.getProductId()))
                .toList();
        validateProducts(order, products);
    }

    private void validateClient(Order order, ClientResponse client) {
        if (client == null) {
            throw new ClientNotFoundOnClientException(order.getClientId());
        }
    }

    private void validateProducts(Order order, List<ProductResponse> products) {
        Map<Long, ProductResponse> productsById = products.stream()
                .collect(Collectors.toMap(ProductResponse::id, Function.identity()));

        for (OrderItem item : order.getItensList()) {
            ProductResponse product = productsById.get(item.getProductId());

            log.info("Validating product id:{}...", item.getProductId());

            if (product == null) {
                throw new ProductNotFoundOnClientException(item.getProductId());
            }
            log.info("Product id:{} name:{} found in Product-Service.", product.id(), product.name());
            // if (produto.estoque() < item.quantidade()) {
            // throw new EstoqueInsuficienteException(produto.id(), produto.estoque(),
            // item.quantidade());
            // }
        }
    }

    private ClientResponse fetchClient(Long clientId) {
        try {
            return clientServiceClient.findById(clientId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ClientNotFoundOnClientException(clientId);
        } catch (RestClientException e) {
            throw new DownstreamUnavailableException("clients", e);
        }
    }

    private ProductResponse fetchProduct(Long productId) {
        try {
            return productServiceClient.findById(productId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundOnClientException(productId);
        } catch (RestClientException e) {
            throw new DownstreamUnavailableException("products", e);
        }
    }
}

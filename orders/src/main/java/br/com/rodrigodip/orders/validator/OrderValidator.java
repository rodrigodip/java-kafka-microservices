package br.com.rodrigodip.orders.validator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.rodrigodip.orders.client.OrderDataProvider;
import br.com.rodrigodip.orders.client.dto.ClientResponse;
import br.com.rodrigodip.orders.client.dto.ProductResponse;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.entity.OrderItem;
import br.com.rodrigodip.orders.exceptions.ClientNotFoundOnClientException;
import br.com.rodrigodip.orders.exceptions.ProductNotFoundOnClientException;
import br.com.rodrigodip.orders.exceptions.PriceMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValidator {

    private final OrderDataProvider dataProvider;

    public void validate(Order order) {
        ClientResponse client = dataProvider.findClient(order.getClientId());
        validateClient(order, client);

        List<ProductResponse> products = order.getItensList().stream()
                .map(item -> dataProvider.findProduct(item.getProductId()))
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
            if (item.getUnitPrice() == null || product.price().compareTo(item.getUnitPrice()) != 0) {
                throw new PriceMismatchException(item.getProductId(), product.price(), item.getUnitPrice());
            }
            log.info("Product id:{} price [{}] matches.", product.id(), product.price());
        }
    }
}

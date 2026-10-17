package br.com.rodrigodip.orders.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.rodrigodip.orders.client.dto.ClientResponse;
import br.com.rodrigodip.orders.client.dto.OrderInvoicedEvent;
import br.com.rodrigodip.orders.client.dto.OrderItemEvent;
import br.com.rodrigodip.orders.client.dto.OrderPaidEvent;
import br.com.rodrigodip.orders.client.dto.OrderShippedEvent;
import br.com.rodrigodip.orders.client.dto.ProductResponse;
import br.com.rodrigodip.orders.client.dto.mapper.OrderEventMapper;
import br.com.rodrigodip.orders.entity.Order;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderDataAssembler {

    private final OrderDataProvider dataProvider;
    private final OrderEventMapper mapper;

    public OrderPaidEvent assemblePaid(Order order) {
        ClientResponse client = dataProvider.findClient(order.getClientId());
        Map<Long, ProductResponse> productsById = fetchProductsById(order);
        List<OrderItemEvent> itensList = mapItens(order, productsById);
        return mapper.toPaidEvent(order, client, itensList);
    }

    public OrderInvoicedEvent assembleInvoiced(Order order) {
        ClientResponse client = dataProvider.findClient(order.getClientId());
        Map<Long, ProductResponse> productsById = fetchProductsById(order);
        List<OrderItemEvent> itensList = mapItens(order, productsById);
        return mapper.toInvoicedEvent(order, client, itensList);
    }

    public OrderShippedEvent assembleShipped(Order order) {
        ClientResponse client = dataProvider.findClient(order.getClientId());
        Map<Long, ProductResponse> productsById = fetchProductsById(order);
        List<OrderItemEvent> itensList = mapItens(order, productsById);
        return mapper.toShippedEvent(order, client, itensList);
    }

    private Map<Long, ProductResponse> fetchProductsById(Order order) {
        return order.getItensList().stream()
                .collect(Collectors.toMap(
                        item -> item.getProductId(),
                        item -> dataProvider.findProduct(item.getProductId()),
                        (a, b) -> a));
    }

    private List<OrderItemEvent> mapItens(Order order, Map<Long, ProductResponse> productsById) {
        return order.getItensList().stream()
                .map(item -> mapper.toEventItem(item, productsById.get(item.getProductId())))
                .toList();
    }
}

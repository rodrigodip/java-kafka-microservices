package br.com.rodrigodip.orders.client.dto.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.orders.client.dto.ClientResponse;
import br.com.rodrigodip.orders.client.dto.OrderInvoicedEvent;
import br.com.rodrigodip.orders.client.dto.OrderItemEvent;
import br.com.rodrigodip.orders.client.dto.OrderPaidEvent;
import br.com.rodrigodip.orders.client.dto.OrderShippedEvent;
import br.com.rodrigodip.orders.client.dto.ProductResponse;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.clientId", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.cpf", target = "clientCpf")
    @Mapping(source = "client.email", target = "clientEmail")
    @Mapping(source = "client.phone", target = "clientPhone")
    @Mapping(source = "client.address", target = "clientAddress")
    @Mapping(source = "client.number", target = "clientAddressNumber")
    @Mapping(source = "client.zipcode", target = "clientZipcode")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "order.total", target = "total")
    @Mapping(source = "itensList", target = "itensList")
    OrderPaidEvent toPaidEvent(Order order, ClientResponse client, List<OrderItemEvent> itensList);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.clientId", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.cpf", target = "clientCpf")
    @Mapping(source = "client.email", target = "clientEmail")
    @Mapping(source = "client.phone", target = "clientPhone")
    @Mapping(source = "client.address", target = "clientAddress")
    @Mapping(source = "client.number", target = "clientAddressNumber")
    @Mapping(source = "client.zipcode", target = "clientZipcode")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "order.total", target = "total")
    @Mapping(source = "order.invoiceUrl", target = "invoiceUrl")
    @Mapping(source = "itensList", target = "itensList")
    OrderInvoicedEvent toInvoicedEvent(Order order, ClientResponse client, List<OrderItemEvent> itensList);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.clientId", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.cpf", target = "clientCpf")
    @Mapping(source = "client.email", target = "clientEmail")
    @Mapping(source = "client.phone", target = "clientPhone")
    @Mapping(source = "client.address", target = "clientAddress")
    @Mapping(source = "client.number", target = "clientAddressNumber")
    @Mapping(source = "client.zipcode", target = "clientZipcode")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "order.total", target = "total")
    @Mapping(source = "itensList", target = "itensList")
    OrderShippedEvent toShippedEvent(Order order, ClientResponse client, List<OrderItemEvent> itensList);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "item.productId", target = "productId")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "item.quantity", target = "quantity")
    @Mapping(source = "item.unitPrice", target = "unitPrice")
    OrderItemEvent toEventItem(OrderItem item, ProductResponse product);
}

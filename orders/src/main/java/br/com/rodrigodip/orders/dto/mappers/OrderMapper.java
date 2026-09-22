package br.com.rodrigodip.orders.dto.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.orders.dto.OrderRequest;
import br.com.rodrigodip.orders.dto.OrderResponse;
import br.com.rodrigodip.orders.dto.PaymentResponse;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.entity.PaymentData;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "itensList", target = "itensList")
    @Mapping(source = "paymentData", target = "paymentData")
    Order map(OrderRequest dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "placedAt", target = "orderDate")
    @Mapping(source = "paymentData", target = "paymentData")
    @Mapping(source = "itensList", target = "itensList")
    OrderResponse toResponse(Order entity);

    PaymentResponse toResponse(PaymentData paymentData);

}
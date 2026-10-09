package br.com.rodrigodip.orders.dto.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.orders.dto.OrderEvent;
import br.com.rodrigodip.orders.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "paymentKey", target = "paymentKey")
    @Mapping(target = "occurredAt", expression = "java(java.time.LocalDateTime.now())")
    OrderEvent toEvent(Order entity);
}

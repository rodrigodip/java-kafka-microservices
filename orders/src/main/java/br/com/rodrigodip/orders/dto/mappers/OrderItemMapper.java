package br.com.rodrigodip.orders.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.orders.dto.OrderItemRequest;
import br.com.rodrigodip.orders.dto.OrderItemResponse;
import br.com.rodrigodip.orders.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem map(OrderItemRequest dto);

    OrderItemResponse toResponse(OrderItem entity);
}
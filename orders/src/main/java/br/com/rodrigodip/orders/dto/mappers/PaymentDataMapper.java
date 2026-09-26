package br.com.rodrigodip.orders.dto.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.orders.dto.PaymentRequest;
import br.com.rodrigodip.orders.dto.PaymentResponse;
import br.com.rodrigodip.orders.entity.PaymentData;

@Mapper(componentModel = "spring")
public interface PaymentDataMapper {

    @Mapping(target = "data")
    @Mapping(target = "paymentMode")
    PaymentData map(PaymentRequest dto);

    PaymentResponse toResponse(PaymentData entity);
}

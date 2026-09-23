package br.com.rodrigodip.products.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.products.dto.ProductRequest;
import br.com.rodrigodip.products.dto.ProductResponse;
import br.com.rodrigodip.products.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "price", target = "price")
    Product toRequest(ProductRequest dto);

    ProductResponse toResponse(Product entity);
}

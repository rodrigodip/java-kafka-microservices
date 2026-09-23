package br.com.rodrigodip.clients.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigodip.clients.dto.ClientRequest;
import br.com.rodrigodip.clients.dto.ClientResponse;
import br.com.rodrigodip.clients.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "cpf", target = "cpf")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "number", target = "number")
    @Mapping(source = "zipcode", target = "zipcode")
    Client toRequest(ClientRequest dto);

    ClientResponse toResponse(Client entity);
}

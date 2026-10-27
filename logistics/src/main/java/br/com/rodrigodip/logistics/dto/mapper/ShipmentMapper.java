package br.com.rodrigodip.logistics.dto.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.rodrigodip.logistics.dto.OrderInvoicedEvent;
import br.com.rodrigodip.logistics.dto.OrderItemEvent;
import br.com.rodrigodip.logistics.dto.OrderShippedEvent;
import br.com.rodrigodip.logistics.dto.ShipmentItemResponse;
import br.com.rodrigodip.logistics.dto.ShipmentResponse;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.entity.ShipmentItem;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShipmentMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "clientName", target = "clientName")
    @Mapping(source = "clientCpf", target = "clientCpf")
    @Mapping(source = "clientEmail", target = "clientEmail")
    @Mapping(source = "clientPhone", target = "clientPhone")
    @Mapping(source = "clientAddress", target = "clientAddress")
    @Mapping(source = "clientAddressNumber", target = "clientAddressNumber")
    @Mapping(source = "clientZipcode", target = "clientZipcode")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "invoiceUrl", target = "invoiceUrl")
    @Mapping(source = "itensList", target = "itensList")
    Shipment toEntity(OrderInvoicedEvent event);

    @AfterMapping
    default void linkItems(@MappingTarget Shipment shipment) {
        if (shipment.getItensList() == null) {
            shipment.setItensList(new java.util.ArrayList<>());
        }
        shipment.getItensList().forEach(item -> item.setShipment(shipment));
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    ShipmentItem toItem(OrderItemEvent event);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "clientName", target = "clientName")
    @Mapping(source = "clientCpf", target = "clientCpf")
    @Mapping(source = "clientEmail", target = "clientEmail")
    @Mapping(source = "clientPhone", target = "clientPhone")
    @Mapping(source = "clientAddress", target = "clientAddress")
    @Mapping(source = "clientAddressNumber", target = "clientAddressNumber")
    @Mapping(source = "clientZipcode", target = "clientZipcode")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "invoiceUrl", target = "invoiceUrl")
    @Mapping(source = "trackingCode", target = "trackingCode")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "shippedAt", target = "shippedAt")
    @Mapping(source = "itensList", target = "itensList")
    ShipmentResponse toResponse(Shipment shipment);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    ShipmentItemResponse toItemResponse(ShipmentItem item);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "clientName", target = "clientName")
    @Mapping(source = "clientCpf", target = "clientCpf")
    @Mapping(source = "clientEmail", target = "clientEmail")
    @Mapping(source = "clientPhone", target = "clientPhone")
    @Mapping(source = "clientAddress", target = "clientAddress")
    @Mapping(source = "clientAddressNumber", target = "clientAddressNumber")
    @Mapping(source = "clientZipcode", target = "clientZipcode")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "total", target = "total")
    @Mapping(source = "trackingCode", target = "trackingCode")
    @Mapping(source = "itensList", target = "itensList")
    OrderShippedEvent toShippedEvent(Shipment shipment);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    OrderItemEvent toItemEvent(ShipmentItem item);
}

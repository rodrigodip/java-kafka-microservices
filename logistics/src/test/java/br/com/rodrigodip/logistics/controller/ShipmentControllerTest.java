package br.com.rodrigodip.logistics.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.rodrigodip.logistics.dto.mapper.ShipmentMapper;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.entity.ShipmentItem;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;
import br.com.rodrigodip.logistics.exceptions.GlobalExceptionHandler;
import br.com.rodrigodip.logistics.exceptions.ShipmentNotFoundException;
import br.com.rodrigodip.logistics.service.ShipmentService;

@ExtendWith(MockitoExtension.class)
class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    private ShipmentMapper shipmentMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        shipmentMapper = Mappers.getMapper(ShipmentMapper.class);
        ShipmentController controller = new ShipmentController(shipmentService, shipmentMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listsPendingShipmentsByDefault() throws Exception {
        when(shipmentService.findPending()).thenReturn(List.of(pendingShipment()));

        mockMvc.perform(get("/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(42))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].itensList[0].name").value("Product"));
    }

    @Test
    void listsShipmentsByStatus() throws Exception {
        Shipment shipment = pendingShipment();
        shipment.markShipped("BR123");
        when(shipmentService.findByStatus(ShipmentStatus.SHIPPED)).thenReturn(List.of(shipment));

        mockMvc.perform(get("/shipments").param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SHIPPED"));

        verify(shipmentService).findByStatus(ShipmentStatus.SHIPPED);
    }

    @Test
    void findsShipmentByOrderId() throws Exception {
        when(shipmentService.findByOrderId(42L)).thenReturn(pendingShipment());

        mockMvc.perform(get("/shipments/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(42))
                .andExpect(jsonPath("$.trackingCode").doesNotExist());
    }

    @Test
    void registersTrackingCodeAndShipsOrder() throws Exception {
        Shipment shipment = pendingShipment();
        shipment.markShipped("BR123456789");
        when(shipmentService.ship(42L, "BR123456789")).thenReturn(shipment);

        mockMvc.perform(put("/shipments/42/tracking-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"trackingCode\":\"BR123456789\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(42))
                .andExpect(jsonPath("$.status").value("SHIPPED"))
                .andExpect(jsonPath("$.trackingCode").value("BR123456789"))
                .andExpect(jsonPath("$.shippedAt").isNotEmpty());
    }

    @Test
    void rejectsBlankTrackingCode() throws Exception {
        mockMvc.perform(put("/shipments/42/tracking-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"trackingCode\":\" \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownShipment() throws Exception {
        when(shipmentService.findByOrderId(99L)).thenThrow(new ShipmentNotFoundException(99L));

        mockMvc.perform(get("/shipments/99"))
                .andExpect(status().isNotFound());
    }

    private Shipment pendingShipment() {
        Shipment shipment = new Shipment();
        shipment.setOrderId(42L);
        shipment.setClientId(1L);
        shipment.setClientName("Client");
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setTotal(new BigDecimal("100.00"));
        shipment.setInvoiceUrl("http://invoicing/invoices/42/download");

        ShipmentItem item = new ShipmentItem();
        item.setProductId(1L);
        item.setName("Product");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        shipment.addItem(item);

        return shipment;
    }
}

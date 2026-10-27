package br.com.rodrigodip.logistics.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.logistics.dto.ShipmentResponse;
import br.com.rodrigodip.logistics.dto.TrackingCodeRequest;
import br.com.rodrigodip.logistics.dto.mapper.ShipmentMapper;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;
import br.com.rodrigodip.logistics.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;

    @GetMapping
    public ResponseEntity<List<ShipmentResponse>> findAll(
            @RequestParam(required = false) ShipmentStatus status) {

        List<Shipment> shipments = status == null
                ? shipmentService.findPending()
                : shipmentService.findByStatus(status);

        return ResponseEntity.ok(shipments.stream()
                .map(shipmentMapper::toResponse)
                .toList());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ShipmentResponse> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shipmentMapper.toResponse(shipmentService.findByOrderId(orderId)));
    }

    @PutMapping("/{orderId}/tracking-code")
    public ResponseEntity<ShipmentResponse> updateTrackingCode(
            @PathVariable Long orderId,
            @Valid @RequestBody TrackingCodeRequest request) {

        Shipment shipment = shipmentService.ship(orderId, request.trackingCode());

        return ResponseEntity.ok(shipmentMapper.toResponse(shipment));
    }
}

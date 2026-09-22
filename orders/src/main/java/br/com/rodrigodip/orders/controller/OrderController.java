package br.com.rodrigodip.orders.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.orders.dto.OrderRequest;
import br.com.rodrigodip.orders.dto.OrderResponse;
import br.com.rodrigodip.orders.dto.mappers.OrderMapper;
import br.com.rodrigodip.orders.service.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(@RequestBody OrderRequest request) {

        var order = mapper.map(request);
        var savedOrder = orderService.saveOrder(order);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(savedOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        OrderResponse response = mapper.toResponse(orderService.findById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {

        List<OrderResponse> ordersList = orderService.findAll()
                .stream().map(order -> mapper.toResponse(order)).toList();

        return ResponseEntity.ok(ordersList);
    }
}

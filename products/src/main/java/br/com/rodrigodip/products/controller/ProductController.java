package br.com.rodrigodip.products.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.products.dto.ProductRequest;
import br.com.rodrigodip.products.dto.ProductResponse;
import br.com.rodrigodip.products.dto.mapper.ProductMapper;
import br.com.rodrigodip.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper mapper;

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest request) {

        var product = mapper.toRequest(request);
        var savedProduct = productService.save(product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(savedProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {

        ProductResponse response = mapper.toResponse(productService.findById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {

        List<ProductResponse> productList = productService.findAll()
                .stream().map(product -> mapper.toResponse(product)).toList();

        return ResponseEntity.ok(productList);
    }
}

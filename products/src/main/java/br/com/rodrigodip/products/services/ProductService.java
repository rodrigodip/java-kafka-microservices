package br.com.rodrigodip.products.services;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.products.dto.ProductRequest;
import br.com.rodrigodip.products.dto.ProductResponse;
import br.com.rodrigodip.products.entities.Product;
import br.com.rodrigodip.products.exceptions.ProductNotFoundException;
import br.com.rodrigodip.products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse save(ProductRequest request) {

        Product product = new Product();

        product.setName(request.name());
        product.setPrice(request.price());

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getPrice());
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice());
    }
}
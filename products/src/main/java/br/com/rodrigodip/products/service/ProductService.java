package br.com.rodrigodip.products.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.products.entity.Product;
import br.com.rodrigodip.products.exceptions.ProductNotFoundException;
import br.com.rodrigodip.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product save(Product product) {

        return productRepository.save(product);
    }

    public Product findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return product;
    }

    public List<Product> findAll() {
        List<Product> productList = productRepository.findAll();
        return productList;
    }
}
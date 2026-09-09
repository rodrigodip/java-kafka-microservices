package br.com.rodrigodip.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.products.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}

package br.com.rodrigodip.products.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.products.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}

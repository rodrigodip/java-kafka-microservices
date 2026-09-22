package br.com.rodrigodip.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.orders.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}

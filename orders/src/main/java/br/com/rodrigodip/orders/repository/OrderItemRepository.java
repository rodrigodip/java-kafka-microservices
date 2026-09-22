package br.com.rodrigodip.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.orders.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}

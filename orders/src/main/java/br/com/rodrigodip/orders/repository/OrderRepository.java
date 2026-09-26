package br.com.rodrigodip.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.orders.entity.Order;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndPaymentKey(Long id, String paymentKey);
}

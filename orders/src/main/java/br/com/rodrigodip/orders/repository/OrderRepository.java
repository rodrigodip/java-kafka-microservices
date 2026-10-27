package br.com.rodrigodip.orders.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.rodrigodip.orders.entity.Order;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndPaymentKey(Long id, String paymentKey);

    @EntityGraph(attributePaths = "itensList")
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}

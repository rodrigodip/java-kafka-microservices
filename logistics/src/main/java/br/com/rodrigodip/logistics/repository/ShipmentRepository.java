package br.com.rodrigodip.logistics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    @EntityGraph(attributePaths = "itensList")
    Optional<Shipment> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    @EntityGraph(attributePaths = "itensList")
    List<Shipment> findByStatusOrderByCreatedAtAsc(ShipmentStatus status);
}

package br.com.rodrigodip.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.orders.outbox.OutboxEvent;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByPublishedAtIsNullOrderByIdAsc();
}

package br.com.rodrigodip.logistics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.logistics.outbox.OutboxEvent;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByPublishedAtIsNullOrderByIdAsc();
}

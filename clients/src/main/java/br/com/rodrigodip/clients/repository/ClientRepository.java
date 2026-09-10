package br.com.rodrigodip.clients.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigodip.clients.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}

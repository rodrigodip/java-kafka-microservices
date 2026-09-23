package br.com.rodrigodip.clients.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.clients.entity.Client;
import br.com.rodrigodip.clients.exceptions.ClientNotFoundException;
import br.com.rodrigodip.clients.repository.ClientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client save(Client client) {

        return clientRepository.save(client);
    }

    public Client findById(Long id) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return client;
    }

    public List<Client> findAll() {

        return clientRepository.findAll();
    }
}
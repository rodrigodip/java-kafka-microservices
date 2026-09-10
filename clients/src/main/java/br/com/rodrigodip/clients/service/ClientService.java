package br.com.rodrigodip.clients.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.clients.dto.ClientRequest;
import br.com.rodrigodip.clients.dto.ClientResponse;
import br.com.rodrigodip.clients.entity.Client;
import br.com.rodrigodip.clients.exceptions.ClientNotFoundException;
import br.com.rodrigodip.clients.repository.ClientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponse save(ClientRequest request) {

        Client client = new Client(
                request.name(),
                request.cpf(),
                request.email(),
                request.phone(),
                request.address(),
                request.number(),
                request.zipcode());

        Client savedClient = clientRepository.save(client);

        return new ClientResponse(
                savedClient.getId(),
                savedClient.getName(),
                savedClient.getCpf(),
                savedClient.getEmail(),
                savedClient.getPhone(),
                savedClient.getAddress(),
                savedClient.getNumber(),
                savedClient.getZipcode());
    }

    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getCpf(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                client.getNumber(),
                client.getZipcode());
    }

    public List<ClientResponse> findAll() {
        List<Client> clientList = clientRepository.findAll();
        List<ClientResponse> responseList = new ArrayList<>();

        for (Client client : clientList) {
            ClientResponse response = new ClientResponse(
                    client.getId(),
                    client.getName(),
                    client.getCpf(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getNumber(),
                    client.getZipcode());

            responseList.add(response);
        }
        return responseList;
    }
}
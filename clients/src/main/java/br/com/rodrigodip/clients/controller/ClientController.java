package br.com.rodrigodip.clients.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigodip.clients.dto.ClientRequest;
import br.com.rodrigodip.clients.dto.ClientResponse;
import br.com.rodrigodip.clients.dto.mapper.ClientMapper;
import br.com.rodrigodip.clients.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper mapper;

    @PostMapping
    public ResponseEntity<ClientResponse> saveClient(@Valid @RequestBody ClientRequest request) {

        var client = mapper.toRequest(request);
        var savedClient = clientService.save(client);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(savedClient));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {

        ClientResponse client = mapper.toResponse(clientService.findById(id));
        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {

        List<ClientResponse> ClientList = clientService.findAll()
                .stream().map(client -> mapper.toResponse(client)).toList();
        return ResponseEntity.ok(ClientList);
    }
}

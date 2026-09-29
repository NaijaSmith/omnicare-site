package com.omnicare.modules.client.controller;

import com.omnicare.modules.client.dto.CreateClientRequest;
import com.omnicare.modules.client.model.Client;
import com.omnicare.modules.client.model.ClientStatus;
import com.omnicare.modules.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody CreateClientRequest request) {
        return new ResponseEntity<>(clientService.registerClient(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Client> updateStatus(@PathVariable Long id, @RequestParam ClientStatus status) {
        return ResponseEntity.ok(clientService.updateStatus(id, status));
    }
}

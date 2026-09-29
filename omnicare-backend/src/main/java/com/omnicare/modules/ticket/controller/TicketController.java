package com.omnicare.modules.ticket.controller;

import com.omnicare.modules.ticket.dto.CreateTicketRequest;
import com.omnicare.modules.ticket.model.Ticket;
import com.omnicare.modules.ticket.model.TicketStatus;
import com.omnicare.modules.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return new ResponseEntity<>(ticketService.createTicket(request), HttpStatus.CREATED);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Ticket>> getTicketsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(ticketService.getTicketsByClient(clientId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }
}

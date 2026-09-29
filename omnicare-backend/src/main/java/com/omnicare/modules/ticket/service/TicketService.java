package com.omnicare.modules.ticket.service;

import com.omnicare.common.audit.AuditLog;
import com.omnicare.common.audit.AuditLogRepository;
import com.omnicare.common.exception.ResourceNotFoundException;
import com.omnicare.modules.client.model.Client;
import com.omnicare.modules.client.repository.ClientRepository;
import com.omnicare.modules.ticket.dto.CreateTicketRequest;
import com.omnicare.modules.ticket.model.Ticket;
import com.omnicare.modules.ticket.model.TicketStatus;
import com.omnicare.modules.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public Ticket createTicket(CreateTicketRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + request.getClientId()));

        Ticket ticket = Ticket.builder()
                .client(client)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Ticket")
                .entityId(savedTicket.getId())
                .actionType("CREATE")
                .performedBy("SYSTEM_ADMIN")
                .details("Created ticket: " + savedTicket.getTitle() + " for client " + client.getBusinessName())
                .build());

        return savedTicket;
    }

    public List<Ticket> getTicketsByClient(Long clientId) {
        return ticketRepository.findByClientId(clientId);
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        ticket.setStatus(newStatus);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Ticket")
                .entityId(ticket.getId())
                .actionType("UPDATE_STATUS")
                .performedBy("SYSTEM_ADMIN")
                .details("Ticket status updated to " + newStatus)
                .build());

        return ticketRepository.save(ticket);
    }
}

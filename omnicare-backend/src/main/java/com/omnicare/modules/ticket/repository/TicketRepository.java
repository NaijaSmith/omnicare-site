package com.omnicare.modules.ticket.repository;

import com.omnicare.modules.ticket.model.Ticket;
import com.omnicare.modules.ticket.model.TicketPriority;
import com.omnicare.modules.ticket.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClientId(Long clientId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriorityAndStatus(TicketPriority priority, TicketStatus status);
}
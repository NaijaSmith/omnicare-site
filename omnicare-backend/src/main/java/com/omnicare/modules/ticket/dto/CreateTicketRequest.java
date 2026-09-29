package com.omnicare.modules.ticket.dto;

import com.omnicare.modules.ticket.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTicketRequest {
    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotBlank(message = "Ticket title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private TicketPriority priority = TicketPriority.MEDIUM;
}

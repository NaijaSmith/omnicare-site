package com.omnicare.modules.client.service;

import com.omnicare.common.audit.AuditLog;
import com.omnicare.common.audit.AuditLogRepository;
import com.omnicare.common.exception.ResourceNotFoundException;
import com.omnicare.modules.client.dto.CreateClientRequest;
import com.omnicare.modules.client.model.Client;
import com.omnicare.modules.client.model.ClientStatus;
import com.omnicare.modules.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public Client registerClient(CreateClientRequest request) {
        Client client = Client.builder()
                .businessName(request.getBusinessName())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .physicalAddress(request.getPhysicalAddress())
                .status(ClientStatus.PROSPECT)
                .build();

        Client savedClient = clientRepository.save(client);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Client")
                .entityId(savedClient.getId())
                .actionType("CREATE")
                .performedBy("SYSTEM_ADMIN")
                .details("Registered new client: " + savedClient.getBusinessName())
                .build());

        return savedClient;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
    }

    @Transactional
    public Client updateStatus(Long id, ClientStatus newStatus) {
        Client client = getClientById(id);
        client.setStatus(newStatus);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Client")
                .entityId(client.getId())
                .actionType("UPDATE_STATUS")
                .performedBy("SYSTEM_ADMIN")
                .details("Updated status to: " + newStatus)
                .build());

        return clientRepository.save(client);
    }
}

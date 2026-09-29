package com.omnicare.modules.billing.service;

import com.omnicare.common.audit.AuditLog;
import com.omnicare.common.audit.AuditLogRepository;
import com.omnicare.common.exception.ResourceNotFoundException;
import com.omnicare.modules.billing.model.Invoice;
import com.omnicare.modules.billing.model.InvoiceStatus;
import com.omnicare.modules.billing.repository.InvoiceRepository;
import com.omnicare.modules.client.model.Client;
import com.omnicare.modules.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public Invoice generateMonthlyRetainerInvoice(Long clientId, BigDecimal amount) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));

        String invoiceNum = "INV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Invoice invoice = Invoice.builder()
                .client(client)
                .invoiceNumber(invoiceNum)
                .amount(amount)
                .status(InvoiceStatus.UNPAID)
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Invoice")
                .entityId(savedInvoice.getId())
                .actionType("GENERATE")
                .performedBy("SYSTEM_ADMIN")
                .details("Generated retainer invoice " + invoiceNum + " for " + client.getBusinessName())
                .build());

        return savedInvoice;
    }

    @Transactional
    public Invoice markAsPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        invoice.setStatus(InvoiceStatus.PAID);

        auditLogRepository.save(AuditLog.builder()
                .entityName("Invoice")
                .entityId(invoice.getId())
                .actionType("UPDATE_PAYMENT")
                .performedBy("SYSTEM_ADMIN")
                .details("Invoice " + invoice.getInvoiceNumber() + " marked as PAID")
                .build());

        return invoiceRepository.save(invoice);
    }
}

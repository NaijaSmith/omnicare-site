package com.omnicare.modules.billing.repository;

import com.omnicare.modules.billing.model.Invoice;
import com.omnicare.modules.billing.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByClientId(Long clientId);
    List<Invoice> findByStatus(InvoiceStatus status);
}

package com.omnicare.modules.billing.controller;

import com.omnicare.modules.billing.model.Invoice;
import com.omnicare.modules.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate")
    public ResponseEntity<Invoice> generateInvoice(@RequestParam Long clientId, @RequestParam BigDecimal amount) {
        return new ResponseEntity<>(invoiceService.generateMonthlyRetainerInvoice(clientId, amount), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }
}

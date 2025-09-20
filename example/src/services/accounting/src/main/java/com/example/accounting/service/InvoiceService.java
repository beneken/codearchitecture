package com.example.accounting.service;

import com.example.accounting.model.invoice.Invoice;
import com.example.accounting.model.invoice.InvoiceRepository;
import com.example.accounting.model.invoice.InvoiceItem;
import com.example.accounting.model.invoice.InvoiceItemId;
import com.example.accounting.model.invoice.InvoiceItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private final InvoiceItemRepository invoiceItemRepository;

    private final SupplierService supplierService;

    public void processInvoiceItems(Invoice savedInvoice, List<InvoiceItem> items) {

        for (int i = 0; i < items.size(); i++) {
            InvoiceItem item = items.get(i);
            item.setInvoice(savedInvoice);
            item.setId(new InvoiceItemId(savedInvoice, i + 1));

            invoiceItemRepository.save(item); // Speichern des Objekts
            savedInvoice.getInvoiceItems().add(item);
        }
    }

    public Optional<Invoice> createInvoice(Invoice invoice) {
        if (invoiceRepository.findByInvoiceNumber(invoice.getInvoiceNumber()).isPresent()) {
            return Optional.empty();
        }

        invoice.setSupplier(supplierService.createOrGetSupplier(invoice.getSupplier()));
        List<InvoiceItem> items = invoice.getInvoiceItems();
        invoice.setInvoiceItems(new ArrayList<>());
        Invoice savedInvoice = invoiceRepository.save(invoice);

        processInvoiceItems(savedInvoice, items);

        return Optional.of(savedInvoice);
    }
}
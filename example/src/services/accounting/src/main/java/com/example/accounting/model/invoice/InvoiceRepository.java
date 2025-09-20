package com.example.accounting.model.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByIsCheckedIsNull();
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
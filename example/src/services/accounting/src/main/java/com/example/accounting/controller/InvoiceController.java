package com.example.accounting.controller;

import com.example.accounting.model.invoice.Invoice;
import com.example.accounting.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/invoice")
@Validated
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "create a new invoice", description = "creates new invoice.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invoice created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Client error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Id already in use", content = @Content),
    })
    @PostMapping("")
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest invoice) {
        Optional<Invoice> created = invoiceService.createInvoice(invoice.invoice);
        return created.map(value -> ResponseEntity.created(URI.create("/invoice/" + value.getInvoiceId()))
                .body(created.get())).orElseGet(() -> ResponseEntity.status(409).build());
    }

    public record InvoiceRequest(Invoice invoice) {
    }

}
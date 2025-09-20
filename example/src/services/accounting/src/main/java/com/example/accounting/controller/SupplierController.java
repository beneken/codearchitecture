package com.example.accounting.controller;

import com.example.accounting.model.supplier.Supplier;
import com.example.accounting.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "get all suppliers", description = "Returns List of all known Suppliers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of all suppliers", content = @Content),
    })
    @GetMapping("")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @Operation(summary = "Find a supplier by id", description = "Returns a single supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found", content = @Content),
            @ApiResponse(responseCode = "404", description = "No Supplier with this ID", content = @Content),
    })
    @GetMapping("/{supplierId}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long supplierId) {
        Optional<Supplier> customer = supplierService.getSupplierById(supplierId);
        return customer
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
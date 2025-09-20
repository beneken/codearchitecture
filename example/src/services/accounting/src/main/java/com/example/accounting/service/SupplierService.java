package com.example.accounting.service;

import com.example.accounting.model.supplier.Supplier;
import com.example.accounting.model.supplier.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public Supplier createOrGetSupplier(Supplier customer) {
        return supplierRepository.findSupplierByCompanyNameAndAddressStreet(customer.getCompanyName(), customer.getAddressStreet())
                .orElseGet(() -> supplierRepository.save(customer));
    }
}
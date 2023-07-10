package com.example.internaldependencies.controller;

import com.example.internaldependencies.repositories.Customer;
import com.example.internaldependencies.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CustomerController {
    private CustomerService service;
    @Autowired // Dependency Injection
    public CustomerController(CustomerService service) {
       this.service = service;
    }

    @GetMapping(value="/customers/{id}")
    public ResponseEntity<CustomerDTO> findCustomerById(@PathVariable("id") Long id) {
        Optional<Customer> customer = service.findCustomerById(id);
        if (customer.isEmpty()) return ResponseEntity.notFound().build();


        CustomerDTO result = new CustomerDTO(customer.get().getId(),
                customer.get().getFirstName(),
                customer.get().getLastName());
        return ResponseEntity.ok(result);
    }


}

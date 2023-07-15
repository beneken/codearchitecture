package com.example.internaldependencies.services;

import com.example.internaldependencies.models.Customer;
import com.example.internaldependencies.models.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private CustomerRepository repository;

    @Autowired // Dependency Injection
    public CustomerService (CustomerRepository repository) {
        this.repository=repository;
    }

    public Optional<Customer> findCustomerById(Long id) {
        if (id == 2) { // Some Data to demonstrate
            return Optional.of(new Customer("Fred", "Feuerstein"));
        }
        return repository.findById(id);
    }
}

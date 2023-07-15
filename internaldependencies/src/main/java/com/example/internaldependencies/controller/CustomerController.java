package com.example.internaldependencies.controller;

import com.example.internaldependencies.models.Customer;
import com.example.internaldependencies.services.CustomerService;
import com.example.internaldependencies.swapi.Person;
import com.example.internaldependencies.swapi.StarWars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CustomerController {
    private CustomerService service;
    private StarWars swapi;
    @Autowired // Dependency Injection
    public CustomerController(CustomerService service, StarWars swapiProxy) {
        this.swapi = swapiProxy;
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

    @GetMapping(value="/starwars/{id}")
    public ResponseEntity<Person> findAlienById(@PathVariable("id") Long id) {
        Optional<Person> person = swapi.findPersonById(id);
        return person.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

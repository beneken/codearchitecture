package com.example.internaldependencies.controller;

import com.example.internaldependencies.swapi.Person;
import com.example.internaldependencies.swapi.StarWars;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomerControllerTest {
    private CustomerController controllerUnderTest;
    private StarWars mockProxy;

    @BeforeEach
    void setup() {
        mockProxy = mock(StarWars.class);
        controllerUnderTest = new CustomerController(null, mockProxy);
    }

    @Test
    void findAlienById() {
        Person expected = new Person();
        expected.setName("Luke");
        when(mockProxy.findPersonById(1L)).thenReturn(Optional.of(expected));
        ResponseEntity<Person> result = controllerUnderTest.findAlienById(1L);
        assertEquals(result.getBody(),expected);
        verify(mockProxy, times(1)).findPersonById(1L);
    }
}
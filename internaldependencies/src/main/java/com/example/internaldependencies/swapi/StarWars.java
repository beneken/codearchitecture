package com.example.internaldependencies.swapi;

import java.util.Optional;

public interface StarWars {
    Optional<Person> findPersonById(Long id);
}

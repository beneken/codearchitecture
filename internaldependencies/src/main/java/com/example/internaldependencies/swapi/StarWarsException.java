package com.example.internaldependencies.swapi;

public class StarWarsException extends Exception {
    public StarWarsException(Exception x) {
        super(x);
    }
    public StarWarsException(String reason) {
        super(reason);
    }
}

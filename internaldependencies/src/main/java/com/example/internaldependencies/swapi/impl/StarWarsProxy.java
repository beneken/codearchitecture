package com.example.internaldependencies.swapi.impl;

import com.example.internaldependencies.swapi.Person;
import com.example.internaldependencies.swapi.StarWars;
import com.example.internaldependencies.swapi.StarWarsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Service
public class StarWarsProxy implements StarWars {
    private static final Logger logger = LoggerFactory.getLogger(StarWarsProxy.class);

    private static final String SWAPI_BASEURL = "https://swapi.dev/api/";

    private HttpClient client;

    public StarWarsProxy() {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public Optional<Person> findPersonById(Long id) throws StarWarsException{
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(SWAPI_BASEURL + "people/" + id))
                    .headers("Accept",
                            "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            Person result = mapper.readValue(response.body(), Person.class);
            return Optional.of(result);

        } catch (Exception x) {
            logger.error("Error in finding {}", x.getMessage(), x);
            throw new StarWarsException(x);
        }
        return Optional.empty();
    }
}
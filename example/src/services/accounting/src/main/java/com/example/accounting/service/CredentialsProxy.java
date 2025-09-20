package com.example.accounting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CredentialsProxy {
    private static final Logger logger = LoggerFactory.getLogger(CredentialsProxy.class);
    private static final String API_BASE_URL = "<Base URL of API>";
    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();

    private String apiKey;

    public String getApiKey(String username, String password) {
        if (apiKey == null) {
            try {
                String jwt = login(username, password);

                logger.info("getting API-Key for user: {}", username);

                String requestBody = "";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_BASE_URL + "/api/v1/ApiKey"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + jwt)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                apiKey = sendRequest(request, "API-Key");
            } catch (Exception e) {
                throw new RuntimeException("Failed to retrieve API key", e);
            }
        }
        return apiKey;
    }

    private String login(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        logger.info("Logging in for user {}", username);
        String requestBody = "{\"username\"=" + username + ", \"password\"="+password+" }";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_BASE_URL + "/api/v1/Login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return sendRequest(request, "<TODO>");
    }

    private String sendRequest(HttpRequest request, String context) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.error("Error in {}: {}", context, response.body());
            throw new IOException("Error in " + context + ": " + response.body());
        }
        return response.body();
    }

}
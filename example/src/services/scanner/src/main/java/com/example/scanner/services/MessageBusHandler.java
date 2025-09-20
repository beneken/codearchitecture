package com.example.scanner.services;

import com.example.shared.mom.RabbitMQConnector;
import com.example.shared.mom.RabbitMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class MessageBusHandler  {
    private static final Logger log = LoggerFactory.getLogger(MessageBusHandler.class);
    private final RabbitMQConnector rabbitMQConnector;

    public MessageBusHandler(RabbitMQConnector rabbitMQConnector) {
        this.rabbitMQConnector = rabbitMQConnector;
    }

    public CompletableFuture<Void> send(String pdfContent) {
        return CompletableFuture.runAsync(() -> {
            try {
                RabbitMQQueue queue = rabbitMQConnector.getQueue("InvoiceInput");
                log.info("Sending parsed PDF content to queue 'InvoiceInput'");
                queue.publish(pdfContent);
                log.info("Successfully sent message to queue 'InvoiceInput'");
            } catch (IOException e) {
                log.error("Error accessing 'InvoiceInput': {}", e.getMessage(),e);
            }
        });
    }
}
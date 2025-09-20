package com.example.scanner.services;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class DocumentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessor.class);
    private final BlockingQueue<String> newDocuments;
    private final MessageBusHandler messageBus;

    public DocumentProcessor(@NotNull BlockingQueue<String> newDocuments, MessageBusHandler messageBus) {
        this.newDocuments = newDocuments;
        this.messageBus = messageBus;
    }

    public void sendNewDocument() {
        try {
           String document = newDocuments.take();
           String truncatedJson = document.substring(0, Math.min(50, document.length())) + "...";
           logger.info("Document from queue: {} (remaining items: {})", truncatedJson, newDocuments.size());
           processDocument(document);
        }
        catch (InterruptedException e) {
            logger.info("DocumentProcessor thread was interrupted");
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            logger.error("Unexpected error in DocumentProcessor", e);
        }
    }

    private void processDocument(String json) {
        try {
            logger.debug("Processing content of length: {}", json.length());
            messageBus.send(json).join(); // Sende JSON an RabbitMQ
            logger.info("Sent to RabbitMQ");
        } catch (Exception e) {
            logger.error("Error in processing: {}", e.getMessage(), e);
        }
    }
}
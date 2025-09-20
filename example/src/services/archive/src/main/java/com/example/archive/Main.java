package com.example.archive;

import com.example.shared.config.IConfiguration;
import com.example.shared.config.impl.Configuration;
import com.example.shared.mom.RabbitMQConfig;
import com.example.shared.mom.RabbitMQConnector;
import com.example.shared.mom.RabbitMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String PERSISTENCE_PATH_ENV = "PERSISTENCE_PATH";

    public static void main(String[] args) {
        IConfiguration config = new Configuration();
        RabbitMQConnector connector;

        try {
            Path path = config.getPath(PERSISTENCE_PATH_ENV);
            RabbitMQConfig rabbitMQConfig = new RabbitMQConfig(config);
            connector = new RabbitMQConnector(rabbitMQConfig);
            connector.connect();
            logger.info("Erfolgreich mit RabbitMQ verbunden.");

            RabbitMQQueue processedDocuments
                    = connector.getQueue("ProcessedInvoices");

            processedDocuments.consume(message -> {
                try {
                    archiveMessage(message, path);
                } catch (IOException e) {
                    logger.error("Fehler beim Persistieren der Nachricht: {}", message, e);
                }
            });

        } catch (Exception e) {
            logger.error("Fehler beim Initialisieren des DocumentPersistenceService.", e);
        }
    }

    protected static void archiveMessage(String message, Path persistencePath) throws IOException {
        String fileName = "invoice_" + System.currentTimeMillis() + ".json";
        Path filePath = persistencePath.resolve(fileName);

        Files.write(filePath, message.getBytes());
        logger.info("Invoice {} archive", filePath);
    }
}
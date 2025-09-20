package com.example.scanner;

import com.example.scanner.config.ScannerConfig;
import com.example.scanner.services.MessageBusHandler;
import com.example.shared.config.IConfiguration;
import com.example.shared.config.impl.Configuration;
import com.example.scanner.services.DocumentProcessor;
import com.example.scanner.services.DirectoryScanner;
import com.example.shared.mom.RabbitMQConfig;
import com.example.shared.mom.RabbitMQConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try (ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            System.out.println("Starting Scanner Service...");
            logger.info("Starting Scanner Service...");

            IConfiguration config = new Configuration();

            ScannerConfig scannerConfig = new ScannerConfig(config);
            logger.info("Scanner Config: {}", scannerConfig);

            RabbitMQConnector connector = initRabbitMQConnector(config);

            BlockingQueue<String> newDocuments = new LinkedBlockingQueue<>();
            Path path = scannerConfig.getPathToWatchedFolder();
            int maxFileSize = scannerConfig.getMaximumFileSizeInMB();

            DirectoryScanner directoryScanner = new DirectoryScanner(
                    path, maxFileSize, newDocuments);
            directoryScanner.startMonitoring();

            executorService.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        directoryScanner.pollWatchService();
                        Thread.currentThread().sleep(Duration.ofSeconds(10));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            MessageBusHandler messageBus = new MessageBusHandler(connector);
            DocumentProcessor documentProcessor = new DocumentProcessor(newDocuments, messageBus );
            executorService.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    documentProcessor.sendNewDocument();
                }
            });



        } catch (Exception e) {
            logger.error("Error in main: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static RabbitMQConnector initRabbitMQConnector(IConfiguration config) {
        try {
            RabbitMQConfig rabbitMQConfig = new RabbitMQConfig(config);
            logger.info("RabbitMQ Config: {}", rabbitMQConfig);

            RabbitMQConnector connector = new RabbitMQConnector(rabbitMQConfig);
            connector.connect();
            return connector;
        } catch (Exception e) {
            logger.error("Failed to connect to RabbitMQ or initialize the queue. Shutting down the service.", e);
            return null;
        }
    }
}
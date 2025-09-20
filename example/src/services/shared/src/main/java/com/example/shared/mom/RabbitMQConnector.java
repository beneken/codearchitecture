package com.example.shared.mom;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConnector {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConnector.class);

    private final RabbitMQConfig config;
    private final ConnectionFactory factory;
    private Connection connection;

    public RabbitMQConnector(RabbitMQConfig config) {
        this.config = config;

        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.getRabbitMQHost());
        connectionFactory.setPort(config.getRabbitMQPort());
        connectionFactory.setUsername(config.getRabbitMQUsername());
        connectionFactory.setPassword(config.getRabbitMQPassword());
        connectionFactory.setConnectionTimeout(10000);

        factory = connectionFactory;
    }

    public void connect() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            connection = factory.newConnection();
            log.info("Connected to RabbitMQ: {}", config);
        }
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    public RabbitMQQueue getQueue(String name) throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to Rabbit.");
        }
        log.info("Initializing Queue: {}", name);
        return new RabbitMQQueue(connection.createChannel(), name);
    }

    public void close() throws IOException {
        if (connection == null) {
            return;
        }
        connection.close();
        connection = null;
        log.info("Closed RabbitMQ connection.");
    }
}
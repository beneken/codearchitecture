package com.example.shared.mom;

import com.example.shared.config.ConfigurationException;
import com.example.shared.config.IConfiguration;

public class RabbitMQConfig {
    private final String rabbitMQHost;
    private final int rabbitMQPort;
    private final String rabbitMQUsername;
    private final String rabbitMQPassword;

    RabbitMQConfig(String host, int port, String user, String password) {
        this.rabbitMQHost = host;
        this.rabbitMQPort = port;
        this.rabbitMQUsername = user;
        this.rabbitMQPassword = password;
    }

    public RabbitMQConfig(IConfiguration config) throws ConfigurationException {
        this.rabbitMQHost = config.getString("RABBITMQ_HOST");
        this.rabbitMQPort = config.getPort("RABBITMQ_PORT");
        this.rabbitMQUsername = config.getString("RABBITMQ_USERNAME");
        this.rabbitMQPassword = config.getString("RABBITMQ_PASSWORD");
    }

    public String getRabbitMQHost() {
        return rabbitMQHost;
    }
    public int getRabbitMQPort() {
        return rabbitMQPort;
    }
    public String getRabbitMQUsername() {
        return rabbitMQUsername;
    }
    public String getRabbitMQPassword() {
        return rabbitMQPassword;
    }

    @Override
    public String toString() {
        return "RabbitMQConfig{" +
                "Host='" + rabbitMQHost + '\'' +
                ", Port=" + rabbitMQPort +
                ", Username='" + rabbitMQUsername + '\'' +
                ", Password='" + rabbitMQPassword + '\'' +
                '}';
    }
}
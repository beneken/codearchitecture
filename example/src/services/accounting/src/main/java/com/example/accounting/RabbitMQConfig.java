package com.example.accounting;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.example.shared.mom.RabbitMQQueue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig  {
    // Hardcoded for simplicity, should be externalized to application.properties
    private String rabbitMQHost="rabbitmq";
    private String rabbitMQPort="5672";
    private String rabbitMQUsername ="guest";
    private String rabbitMQPassword="guest";
    private String queueName ="DocumentInputQueue";

    @Bean
    public CachingConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQHost);
        int port = 5672; // Standardport
        try {
            port = Integer.parseInt(rabbitMQPort);
        } catch (NumberFormatException e) {
        }
        factory.setPort(port);
        factory.setUsername(rabbitMQUsername);
        factory.setPassword(rabbitMQPassword);

        return new CachingConnectionFactory(factory);
    }

   @Bean
    public Channel rabbitMQChannel(CachingConnectionFactory connectionFactory) throws Exception {
        Connection connection = connectionFactory.getRabbitConnectionFactory().newConnection();
        return connection.createChannel();
    }

    @Bean
    public RabbitMQQueue invoiceInputQueue(Channel rabbitMQChannel) throws Exception {
        return new RabbitMQQueue(rabbitMQChannel, queueName);
    }
}
package com.example.shared.mom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMQQueue {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQQueue.class);
    private final Channel channel;
    private final String queueName;
    private final HashMap<Long, CompletableFuture<Void>> futures = new HashMap<>();

    public RabbitMQQueue(Channel channel, String queueName) throws IOException {
        this.channel = channel;
        this.queueName = queueName;

        initializeQueue();
        registerConfirmListener();
    }

    private void initializeQueue() throws IOException {
        channel.queueDeclare(queueName, true, false, false, null);
        channel.confirmSelect();
    }

    private void registerConfirmListener() {
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                completeFuture(deliveryTag, null);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                completeFutureExceptionally(deliveryTag, new IOException("Nachricht nicht bestätigt"));
            }
        });
    }

    private void completeFuture(long deliveryTag, Void result) {
        CompletableFuture<Void> future = futures.remove(deliveryTag);
        if (future != null) {
            future.complete(result);
        }
    }

    private void completeFutureExceptionally(long deliveryTag, Throwable exception) {
        CompletableFuture<Void> future = futures.remove(deliveryTag);
        if (future != null) {
            future.completeExceptionally(exception);
        }
    }

    public void publish(String message) throws IOException {
        channel.basicPublish("", queueName, null, message.getBytes());
    }

    public CompletableFuture<Void> publishAsync(String message) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            long deliveryTag = channel.getNextPublishSeqNo();
            futures.put(deliveryTag, future);

            log.info("Nachricht wird in Queue {} veröffentlicht, warte auf Bestätigung für Delivery-Tag {}", queueName, deliveryTag);
            channel.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public String consume(Consumer<String> callback) throws IOException {
        return channel.basicConsume(queueName, false, (consumerTag, message) -> {
            String msg = new String(message.getBody());
            log.debug("Neue Nachricht in Queue {}: {}", queueName, msg);
            try {
                callback.accept(msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("Fehler bei der Verarbeitung der Nachricht: {}", msg, e);
                channel.basicNack(message.getEnvelope().getDeliveryTag(), false, true);
            }
        }, consumerTag -> {
        });
    }

    public void cancel(String consumerTag) throws IOException {
        channel.basicCancel(consumerTag);
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
    }
}
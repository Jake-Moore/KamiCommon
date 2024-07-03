package com.kamikazejam.kamicommon.amqp;

import com.kamikazejam.kamicommon.amqp.data.RabbitRpcConsumer;
import com.kamikazejam.kamicommon.amqp.data.RabbitStdConsumer;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * A manager for RabbitMQ connections, channels, and consumers
 * Package-Private for use only by {@link RabbitMQConnector} within {@link RabbitMQAPI} instances.
 */
@SuppressWarnings("unused")
class RabbitMQManager {

    // Connection Information
    private final ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    // Client-Side RPC Information
    private final DeliverCallback rpcClientConsumer;
    private final Map<String, CompletableFuture<String>> rpcPendingRequests = new ConcurrentHashMap<>();

    // Queue Information
    private final Set<String> declaredQueues = new HashSet<>();                         // cache of declared queues
    private final Map<String, BuiltinExchangeType> declaredExchanges = new HashMap<>(); // cache of declared exchanges
    private final Map<String, RabbitStdConsumer> stdConsumers = new HashMap<>();        // cache of standard consumers
    private final Set<String> rpcClientResponseQueues = new HashSet<>();                // cache of client queues for RPC responses
    private final Map<String, RabbitRpcConsumer> rpcConsumers = new HashMap<>();        // cache of server RPC consumers

    RabbitMQManager(String url) {
        factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        factory.setConnectionTimeout(30000);

        try {
            // Configure remote connection url
            factory.setUri(url);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        // Consumes responses from outgoing RPC requests and forwards to CompletableFuture
        //  Every queue can use this generic consumer which uses the correlationId to match requests to responses
        rpcClientConsumer = (consumerTag, delivery) -> {
            String correlationId = delivery.getProperties().getCorrelationId();
            if (correlationId == null) { return; }

            CompletableFuture<String> future = rpcPendingRequests.remove(correlationId);
            if (future == null) { return; }

            future.complete(new String(delivery.getBody(), StandardCharsets.UTF_8));
            getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
    }

    // ---------------------------------------------------------------------------------------------------------------- //
    // ------------------------------------------------- INTERNAL ----------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //

    @ApiStatus.Internal
    private Connection getConnection() {
        if (connection == null || !connection.isOpen()) {
            try {
                connection = factory.newConnection();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    @ApiStatus.Internal
    private Channel getChannel() {
        if (channel == null || !channel.isOpen()) {
            try {
                channel = getConnection().createChannel();
                // Re-Register all Standard Consumers
                for (RabbitStdConsumer c : stdConsumers.values()) {
                    this.registerConsumer(channel, c);
                }
                // Re-Register all RPC Response Consumers
                for (String queue : rpcClientResponseQueues) {
                    this.registerRPCResponseConsumer(channel, queue);
                }
                // Re-Register all RPC Server Consumers
                for (RabbitRpcConsumer c : rpcConsumers.values()) {
                    this.registerConsumer(channel, c);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return channel;
    }
    @ApiStatus.Internal
    private void registerConsumer(@NotNull Channel channel, @NotNull RabbitStdConsumer c) throws IOException {
        getChannel().basicConsume(c.getQueueName(), c.isAutoAck(), c.createConsumer(getChannel()));
    }
    @ApiStatus.Internal
    private void registerConsumer(@NotNull Channel channel, @NotNull RabbitRpcConsumer c) throws IOException {
        getChannel().basicConsume(c.getQueueName(), false, c.getConsumer(), (consumerTag -> {}));
    }
    @ApiStatus.Internal
    private void registerRPCResponseConsumer(@NotNull Channel channel, @NotNull String clientBoundQueue) throws IOException {
        channel.basicConsume(
                clientBoundQueue,                   // queue
                false,                              // autoAck
                UUID.randomUUID().toString(),       // consumerTag
                true,                               // noLocal
                false,                              // exclusive
                null,                               // arguments
                rpcClientConsumer,                  // deliverCallback
                consumerTag -> {}                   // cancelCallback
        );
    }

    // ---------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------------- API METHODS ---------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //
    /**
     * Disconnects the RabbitMQ channel and connection
     */
    public void stop() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isQueueDeclared(@NotNull String queueName) {
        return declaredQueues.contains(queueName);
    }
    public boolean isExchangeDeclared(@NotNull String exchange) {
        return declaredExchanges.containsKey(exchange);
    }

    /**
     * Declares a queue with a default TTL of 60 seconds (iff not declared already)
     * @param queueName the name of the queue to declare
     */
    public void declareQueue(@NotNull String queueName) {
        this.declareQueue(queueName, 60_000);
    }

    /**
     * Declares a queue with a specified TTL (iff not declared already)
     * @param queueName the name of the queue to declare
     * @param TTL_MS the time-to-live of the queue (in milliseconds)
     */
    public void declareQueue(@NotNull String queueName, long TTL_MS) {
        this.declareQueue(queueName, true, false, false, TTL_MS);
    }

    /**
     * Declares a queue with a specified TTL (iff not declared already)
     * @param queueName the name of the queue to declare
     * @param durable whether the queue should survive a broker restart
     * @param exclusive whether the queue should be exclusive to the connection
     * @param autoDelete whether the queue should be auto-deleted when no longer in use
     * @param TTL_MS the time-to-live of the queue (in milliseconds)
     */
    public void declareQueue(@NotNull String queueName, boolean durable, boolean exclusive, boolean autoDelete, long TTL_MS) {
        // Avoid unnecessary re-declarations that cost time
        if (isQueueDeclared(queueName)) { return; }

        try {
            Map<String, Object> args = new HashMap<>();
            args.put("x-message-ttl", TTL_MS); // TTL is time before auto-delete in the queue
            getChannel().queueDeclare(queueName, durable, exclusive, autoDelete, args);
            declaredQueues.add(queueName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Declares an exchange with a provided name and type (iff not declared already)
     * Allows re-declaration of the same exchange name with the same type (does nothing)
     * @param exchange the name of the queue to declare
     */
    public void declareExchange(@NotNull String exchange, BuiltinExchangeType type) {
        // Avoid unnecessary re-declarations that cost time
        if (isExchangeDeclared(exchange)) {
            BuiltinExchangeType existing = declaredExchanges.get(exchange);
            if (!Objects.equals(existing, type)) {
                throw new IllegalStateException("Exchange " + exchange + " is already registered as type: " + existing.getType() + ", cannot register as: " + type.getType());
            }
            // Ignore a re-declaration of the same type
            return;
        }

        try {
            getChannel().exchangeDeclare(exchange, type.getType());
            declaredExchanges.put(exchange, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Acknowledge a message by its delivery tag (see {@link Channel#basicAck(long, boolean)})
     */
    public void basicAck(long deliveryTag, boolean multiple) {
        try {
            getChannel().basicAck(deliveryTag, multiple);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------------- STD METHODS ---------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //
    public void publishMessage(@NotNull String queueName, @NotNull String message) {
        this.publishMessage(queueName, null, message);
    }
    public void publishMessage(@NotNull String queueName, @Nullable AMQP.BasicProperties props, @NotNull String message) {
        // basicPublish(String exchange, String routingKey, AMQP.BasicProperties props, byte[] body)
        try {
            getChannel().basicPublish("", queueName, props, message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void addConsumer(@NotNull RabbitStdConsumer consumer) {
        // Cache the consumer data
        stdConsumers.put(consumer.getQueueName(), consumer);
        // Register the new consumer
        try {
            this.registerConsumer(getChannel(), consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    // ---------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------------- RPC METHODS ---------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //
    public void declareRpcResponseQueue(@NotNull String clientBoundQueue) {
        // Cache the queue name for future re-declarations
        if (rpcClientResponseQueues.contains(clientBoundQueue)) { return; }
        rpcClientResponseQueues.add(clientBoundQueue);
        // Register the new consumer
        try {
            this.registerRPCResponseConsumer(getChannel(), clientBoundQueue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public @NotNull CompletableFuture<String> createRPCRequest(@NotNull String clientBoundQueue, @NotNull String serverBoundQueue, @NotNull String message) {
        String correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(clientBoundQueue)
                .build();

        CompletableFuture<String> future = new CompletableFuture<>();
        rpcPendingRequests.put(correlationId, future);

        try {
            // Publish the request
            getChannel().basicPublish("", serverBoundQueue, props, message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return future;
    }
    public void addConsumer(@NotNull RabbitRpcConsumer consumer) {
        // Cache the consumer data
        rpcConsumers.put(consumer.getQueueName(), consumer);
        // Register the new consumer
        try {
            this.registerConsumer(getChannel(), consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------- //
    // --------------------------------------------- FANOUT METHODS --------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //
    public void publishFanout(@NotNull String exchange, @NotNull String message) {
        this.publishFanout(exchange, null, message);
    }
    public void publishFanout(@NotNull String exchange, @Nullable AMQP.BasicProperties props, @NotNull String message) {
        // basicPublish(String exchange, String routingKey, AMQP.BasicProperties props, byte[] body)
        try {
            declareExchange(exchange, BuiltinExchangeType.FANOUT);
            getChannel().basicPublish(exchange, "", props, message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Declares a queue with a specified TTL (iff not declared already)
     * !! Assumes the exchange is already declared !!
     * @throws IllegalStateException if the exchange is already declared!
     * @param queueName the name of the queue to declare
     */
    @SneakyThrows
    public void declareFanQueue(@NotNull String queueName, @NotNull String exchange) throws IllegalStateException {
        this.declareQueue(queueName); // May throw IllegalStateException
        // Bind this queue to the exchange (assumed to be a FANOUT exchange)
        getChannel().queueBind(queueName, exchange, "");
    }
}

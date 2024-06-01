package com.kamikazejam.kamicommon.amqp;

import com.kamikazejam.kamicommon.amqp.callback.RabbitRpcCallback;
import com.kamikazejam.kamicommon.amqp.callback.RabbitServerCallback;
import com.kamikazejam.kamicommon.amqp.data.RabbitRpcConsumer;
import com.kamikazejam.kamicommon.amqp.data.RabbitRpcQueue;
import com.kamikazejam.kamicommon.amqp.data.RabbitStdConsumer;
import com.rabbitmq.client.AMQP;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class RabbitMQAPI {
    private final @NotNull RabbitMQManager manager;

    // Public Class for API access, but package-private constructor to allow only RabbitMQConnector to manage instances
    RabbitMQAPI(@NotNull RabbitMQManager manager) {
        this.manager = manager;
    }

    // ---------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------------- STD METHODS ---------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //

    /**
     * Acknowledge a message by its delivery tag
     * See {@link com.rabbitmq.client.Channel#basicAck(long, boolean)}
     */
    public void basicAck(long deliveryTag, boolean multiple) {
        manager.basicAck(deliveryTag, multiple);
    }

    /**
     * Publish a message to a queue - does NOT listen for a response
     * @param queueName the name of the queue
     * @param message the message to publish
     */
    public void publishMessage(@NotNull String queueName, @NotNull String message) {
        manager.declareQueue(queueName);
        manager.publishMessage(queueName, message);
    }

    /**
     * Publish a message to a queue (with properties) - does NOT listen for a response
     * @param queueName the name of the queue
     * @param props additional message properties
     * @param message the message to publish
     */
    public void publishMessage(@NotNull String queueName, @NotNull AMQP.BasicProperties props, @NotNull String message) {
        manager.declareQueue(queueName);
        manager.publishMessage(queueName, props, message);
    }

    /**
     * Register a standard consumer (default to auto-acknowledgement)
     * @param queueName the name of the queue
     * @param callback the callback to consume messages
     */
    public void registerStdConsumer(@NotNull String queueName, @NotNull RabbitServerCallback callback) {
        this.registerStdConsumer(queueName, callback, true);
    }

    /**
     * Register a standard consumer
     * @param queueName the name of the queue
     * @param callback the callback to consume messages
     * @param autoAck if messages should be auto-acknowledged
     */
    public void registerStdConsumer(@NotNull String queueName, @NotNull RabbitServerCallback callback, boolean autoAck) {
        manager.declareQueue(queueName);
        manager.addConsumer(new RabbitStdConsumer(queueName, callback, autoAck));
    }


    // ---------------------------------------------------------------------------------------------------------------- //
    // ----------------------------------------------- RPC METHODS ---------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //

    /**
     * Publish a 'RPC' style message and wait for a response
     * @param queue The MQRpcQueue for client-server communication
     * @param message The message to publish
     * @return a CompletableFuture which will be completed when the response is received
     */
    public @NotNull CompletableFuture<String> publishRPC(@NotNull RabbitRpcQueue queue, @NotNull String message) {
        // We consider the caller of this method the 'client' and the 'server' the consumer of this rpc request
        manager.declareQueue(queue.getClientBound());
        manager.declareQueue(queue.getServerBound());

        // 1. We need to register a consumer for the client-bound response
        manager.declareRpcResponseQueue(queue.getClientBound());

        // 2. Send the RPC request while caching a CompletableFuture for the response
        return manager.createRPCRequest(queue.getClientBound(), queue.getServerBound(), message);
    }

    /**
     * Register a 'RPC' style consumer to listen for requests
     * @param queue The MQRpcQueue for client-server communication
     * @param callback The callback to consume messages
     */
    public void registerRpcConsumer(@NotNull RabbitRpcQueue queue, @NotNull RabbitRpcCallback callback) {
        manager.declareQueue(queue.getServerBound());

        // Register the consumer
        manager.addConsumer(new RabbitRpcConsumer(this, queue.getServerBound(), callback));
    }
}

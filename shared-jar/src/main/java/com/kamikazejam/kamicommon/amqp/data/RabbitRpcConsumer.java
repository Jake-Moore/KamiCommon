package com.kamikazejam.kamicommon.amqp.data;

import com.kamikazejam.kamicommon.amqp.RabbitMQAPI;
import com.kamikazejam.kamicommon.amqp.callback.RabbitRpcCallback;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DeliverCallback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@Getter
public class RabbitRpcConsumer {
    private final @NotNull String queueName;
    private final @NotNull RabbitRpcCallback callback;
    private final DeliverCallback consumer;

    public RabbitRpcConsumer(@NotNull RabbitMQAPI api, @NotNull String queueName, @NotNull RabbitRpcCallback callback) {
        this.queueName = queueName;
        this.callback = callback;

        // Configure the delivery here, since it's connection independent
        this.consumer = (consumerTag, delivery) -> {
            // !! Assume all queues were already declared !!

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = callback.consume(new String(delivery.getBody(), StandardCharsets.UTF_8));

            // Publish response and acknowledge the message
            String replyTo = delivery.getProperties().getReplyTo();

            // Use the backing-API to publish the response and acknowledge the message
            api.publishMessage(replyTo, replyProps, response);
            api.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
    }
}

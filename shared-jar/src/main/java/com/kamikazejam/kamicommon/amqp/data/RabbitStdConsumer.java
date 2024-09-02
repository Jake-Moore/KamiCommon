package com.kamikazejam.kamicommon.amqp.data;

import com.kamikazejam.kamicommon.amqp.callback.RabbitServerCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class RabbitStdConsumer {
    private final @NotNull String queueName;
    private final @NotNull RabbitServerCallback callback;
    private final boolean autoAck;
    public RabbitStdConsumer(@NotNull String queueName, @NotNull RabbitServerCallback callback, boolean autoAck) {
        this.queueName = queueName;
        this.callback = callback;
        this.autoAck = autoAck;
    }

    public @NotNull DefaultConsumer createConsumer(@NotNull Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) {
                callback.consume(new String(body));
            }
        };
    }
}

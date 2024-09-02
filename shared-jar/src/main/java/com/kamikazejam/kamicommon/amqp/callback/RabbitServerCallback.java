package com.kamikazejam.kamicommon.amqp.callback;

public interface RabbitServerCallback {
    void consume(String data);
}

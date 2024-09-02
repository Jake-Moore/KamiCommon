package com.kamikazejam.kamicommon.amqp.callback;

import org.jetbrains.annotations.NotNull;

public interface RabbitRpcCallback {
    @NotNull String consume(String data);
}

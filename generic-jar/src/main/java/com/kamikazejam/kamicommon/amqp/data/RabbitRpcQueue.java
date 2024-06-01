package com.kamikazejam.kamicommon.amqp.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a pair of client and server queues for RPC communication.
 * Based on one queue stem, two queues are created: one for client-bound messages and one for server-bound messages.
 */
@Getter
public class RabbitRpcQueue {
    private final @NotNull String clientBound;
    private final @NotNull String serverBound;

    /**
     * Creates a new pair of client and server queues based on the given queue stem.
     * @param queueStem The stem for the queue names
     */
    public RabbitRpcQueue(@NotNull String queueStem) {
        this.clientBound = queueStem + "-client";
        this.serverBound = queueStem + "-server";
    }
}

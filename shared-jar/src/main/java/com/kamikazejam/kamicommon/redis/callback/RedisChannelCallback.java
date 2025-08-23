package com.kamikazejam.kamicommon.redis.callback;

import org.jetbrains.annotations.NotNull;

public interface RedisChannelCallback {
    /**
     * @param channel The channel the message was received on
     * @param message The raw string message received by the channel
     */
    void onMessage(String channel, @NotNull String message);
}

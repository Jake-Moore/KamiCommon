package com.kamikazejam.kamicommon.redis.callback;

import org.jetbrains.annotations.NotNull;

public interface RedisChannelCallback<T> {
    /**
     * @param channel The channel the message was received on
     */
    void onMessage(String channel, @NotNull T message);
}

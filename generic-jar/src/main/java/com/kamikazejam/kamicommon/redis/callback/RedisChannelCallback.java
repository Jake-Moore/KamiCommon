package com.kamikazejam.kamicommon.redis.callback;

public interface RedisChannelCallback<T> {
    /**
     * @param channel The channel the message was received on
     */
    void onMessage(String channel, T message);
}

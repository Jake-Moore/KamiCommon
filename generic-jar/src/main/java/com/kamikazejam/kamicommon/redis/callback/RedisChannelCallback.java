package com.kamikazejam.kamicommon.redis.callback;

public interface RedisChannelCallback<T> {
    void onMessage(T message);
}

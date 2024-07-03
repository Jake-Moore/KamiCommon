package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.callback.RedisChannelCallback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter @SuppressWarnings({"UnusedReturnValue", "unused"})
public class RedisChannel<T> {
    private final @NotNull RedisManager manager;
    private final @NotNull String channel;
    private final @NotNull Class<T> clazz;
    RedisChannel(@NotNull RedisManager manager, @NotNull String channel, @NotNull Class<T> clazz) {
        this.manager = manager;
        this.channel = channel;
        this.clazz = clazz;
    }

    /**
     * Add a callback for this channel (for listening to messages)
     * @return true if the callback was successfully added
     */
    public boolean subscribe(@NotNull RedisChannelCallback<T> callback) {
        return manager.subscribe(callback, clazz, channel);
    }

    public void publishSync(@NotNull T message) {
        manager.publish(channel, message, true);
    }
    public void publishAsync(@NotNull T message) {
        manager.publish(channel, message, false);
    }
    public void publish(@NotNull T message, boolean sync) {
        manager.publish(channel, message, sync);
    }
}

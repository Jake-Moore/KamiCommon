package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.callback.RedisChannelCallback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter @SuppressWarnings({"UnusedReturnValue", "unused"})
public class RedisChannelRaw {
    private final @NotNull RedisManager manager;
    private final @NotNull String channel;
    RedisChannelRaw(@NotNull RedisManager manager, @NotNull String channel) {
        this.manager = manager;
        this.channel = channel;
    }

    /**
     * Add a callback for this channel (for listening to messages)
     * @return true if the callback was successfully added
     */
    public boolean subscribe(@NotNull RedisChannelCallback callback) {
        return manager.subscribeRaw(callback, channel);
    }

    public void publishSync(@NotNull String message) {
        manager.publishRaw(channel, message, true);
    }
    public void publishAsync(@NotNull String message) {
        manager.publishRaw(channel, message, false);
    }
    public void publish(@NotNull String message, boolean sync) {
        manager.publishRaw(channel, message, sync);
    }
}

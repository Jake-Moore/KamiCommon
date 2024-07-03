package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.callback.RedisChannelCallback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter @SuppressWarnings({"UnusedReturnValue", "unused"})
public class RedisMultiChannel<T> {
    private final @NotNull RedisManager manager;
    private final @NotNull List<String> channels;
    private final @NotNull Class<T> clazz;
    RedisMultiChannel(@NotNull RedisManager manager, @NotNull Class<T> clazz, @NotNull String... channels) {
        this.manager = manager;
        this.channels = List.of(channels);
        this.clazz = clazz;
    }

    /**
     * Add a callback for this channel (for listening to messages)
     * @return true if the callback was successfully added
     */
    public boolean subscribe(@NotNull RedisChannelCallback<T> callback) {
        return manager.subscribe(callback, clazz, channels.toArray(new String[0]));
    }

    public void publishSync(@NotNull String channel, @NotNull T message) {
        this.publish(channel, message, true);
    }
    public void publishAsync(@NotNull String channel, @NotNull T message) {
        this.publish(channel, message, false);
    }
    public void publish(@NotNull String channel, @NotNull T message, boolean sync) {
        if (!channels.contains(channel)) {
            throw new IllegalArgumentException("Channel " + channel + " is not part of this RedisMultiChannel");
        }
        manager.publish(channel, message, sync);
    }
}

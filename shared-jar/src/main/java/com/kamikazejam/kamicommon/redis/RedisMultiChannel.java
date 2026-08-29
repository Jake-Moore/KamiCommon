package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.callback.RedisChannelCallback;
import java.util.Arrays;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

@Getter @SuppressWarnings({"UnusedReturnValue", "unused"})
public class RedisMultiChannel {
    private final @NotNull RedisManager manager;
    private final @NotNull List<String> channels;
    RedisMultiChannel(@NotNull RedisManager manager, @NotNull String... channels) {
        this.manager = manager;
        // Copied and frozen, NOT Arrays.asList(channels). asList wraps the caller's array, and
        // @Getter publishes the result: a caller that reuses its array, or calls set() on the
        // getter, would silently change which channels publish() accepts while the actual Redis
        // subscription stays as it was. List.of did copy; this keeps that.
        this.channels = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(channels)));
    }

    /**
     * Add a callback for this channel (for listening to messages)
     * @return true if the callback was successfully added
     */
    public boolean subscribe(@NotNull RedisChannelCallback callback) {
        return manager.subscribe(callback, channels.toArray(new String[0]));
    }

    public void publishSync(@NotNull String channel, @NotNull String message) {
        this.publish(channel, message, true);
    }
    public void publishAsync(@NotNull String channel, @NotNull String message) {
        this.publish(channel, message, false);
    }
    public void publish(@NotNull String channel, @NotNull String message, boolean sync) {
        if (!channels.contains(channel)) {
            throw new IllegalArgumentException("Channel " + channel + " is not part of this RedisMultiChannel");
        }
        manager.publish(channel, message, sync);
    }
}

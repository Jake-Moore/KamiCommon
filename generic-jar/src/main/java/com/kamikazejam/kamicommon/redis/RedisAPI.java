package com.kamikazejam.kamicommon.redis;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class RedisAPI {
    private final @NotNull RedisManager manager;

    // Public Class for API access, but package-private constructor to allow only RabbitMQConnector to manage instances
    RedisAPI(@NotNull RedisManager manager) {
        this.manager = manager;
        this.manager.start();
    }

    // ---------------------------------------------------------------------------------------------------------------- //
    // --------------------------------------------- GENERAL METHODS -------------------------------------------------- //
    // ---------------------------------------------------------------------------------------------------------------- //
    /**
     * Stops the internal Redis Connection
     * This API remains usable, but other methods may incur additional
     *   delays if the connection needs to be re-established
     */
    public void shutdown() {
        manager.shutdown();
    }

    public <T> @NotNull RedisChannel<T> registerChannel(@NotNull String channel, @NotNull Class<T> clazz) {
        return new RedisChannel<>(manager, channel, clazz);
    }

    public RedisCommands<String, String> getCmdsSync() {
        return manager.getRedis().sync();
    }
    public RedisAsyncCommands<String, String> getCmdsAsync() {
        return manager.getRedis().async();
    }
}
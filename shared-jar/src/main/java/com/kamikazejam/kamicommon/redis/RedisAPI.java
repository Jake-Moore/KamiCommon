package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.util.RedisState;
import io.lettuce.core.api.StatefulRedisConnection;
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
     * Releases this caller's claim on the underlying Redis connection.
     *
     * <p>Instances are shared: every caller passing an equal {@link com.kamikazejam.kamicommon.redis.util.RedisConf}
     * receives the same object. The connection is closed once the last holder has called this, so a
     * plugin shutting down no longer takes the connection away from anything else in the same JVM.
     *
     * <p>Once the connection really is closed, this instance is spent. Call
     * {@link RedisConnector#getAPI(com.kamikazejam.kamicommon.redis.util.RedisConf)} again for a
     * fresh one rather than reusing this reference.
     */
    public void shutdown() {
        RedisConnector.release(manager.getConf());
    }

    /** The real teardown, reached only through {@link RedisConnector#release}. */
    void shutdownInternal() {
        manager.shutdown();
    }

    public @NotNull RedisChannel registerChannel(@NotNull String channel) {
        return new RedisChannel(manager, channel);
    }
    public @NotNull RedisMultiChannel registerMultiChannel(@NotNull String... channels) {
        return new RedisMultiChannel(manager, channels);
    }

    public RedisCommands<String, String> getCmdsSync() {
        return manager.getRedis().sync();
    }
    public RedisAsyncCommands<String, String> getCmdsAsync() {
        return manager.getRedis().async();
    }
    public StatefulRedisConnection<String, String> getConnection() {
        return manager.getRedis();
    }

    public RedisState getState() {
        return manager.getState();
    }
    public boolean isConnected() {
        return getState().isEnabled() && getState().isConnected();
    }
}

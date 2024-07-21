package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.util.RedisConf;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The source for all RabbitMQ interactions.
 * Use {@link #getAPI(RedisConf)} to fetch a {@link RedisAPI} instance.
 * All instances are cached by URL for connection/channel reuse.
 */
@SuppressWarnings("unused")
public class RedisConnector {
    // Handle a cache for multiple possible RabbitMQ connections
    private static final Map<RedisConf, RedisManager> managers = new HashMap<>();
    private static RedisManager getManager(RedisConf conf, @Nullable LoggerService logger) {
        if (!managers.containsKey(conf)) {
            managers.put(conf, new RedisManager(conf, logger));
        }
        return managers.get(conf);
    }

    // Handle a cache for RabbitMQAPI instances
    private static final Map<RedisConf, RedisAPI> apis = new HashMap<>();

    /**
     * Get a {@link RedisAPI} instance for the given connection config.
     * @return A new (or cached) {@link RedisAPI}
     */
    public static @NotNull RedisAPI getAPI(@NotNull RedisConf conf) {
        if (!apis.containsKey(conf)) {
            apis.put(conf, new RedisAPI(getManager(conf, null)));
        }
        return apis.get(conf);
    }

    /**
     * Get a {@link RedisAPI} instance for the given connection config.
     * @param logger a {@link LoggerService} to use for logging
     * @return A new (or cached) {@link RedisAPI}
     */
    public static @NotNull RedisAPI getAPI(@NotNull RedisConf conf, @NotNull LoggerService logger) {
        if (!apis.containsKey(conf)) {
            apis.put(conf, new RedisAPI(getManager(conf, logger)));
        }
        return apis.get(conf);
    }
}

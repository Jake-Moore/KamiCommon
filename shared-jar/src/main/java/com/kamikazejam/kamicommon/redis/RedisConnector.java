package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.util.RedisConf;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The source for all Redis interactions.
 * Use {@link #getAPI(RedisConf)} to fetch a {@link RedisAPI} instance.
 * All instances are cached by connection config, so callers describing the same server share one
 * client rather than each opening their own.
 */
@SuppressWarnings("unused")
public class RedisConnector {
    // One API per connection. RedisConf gained equals/hashCode for this: until it did, every lookup
    // missed and every call built another lettuce client, with its own two connections and its own
    // event loop threads, so the reuse described above never happened.
    private static final Map<RedisConf, RedisAPI> apis = new HashMap<>();

    // Sharing one client means one caller's shutdown() would otherwise close the connection out
    // from under every other caller in the same JVM. That is not hypothetical: LuxiousCore's metrics
    // service and LuxiousBridge both call getAPI and both call shutdown, on the same server, against
    // the same Redis. So a client is only really torn down once its last holder releases it.
    private static final Map<RedisConf, Integer> holders = new HashMap<>();

    /**
     * Get a {@link RedisAPI} instance for the given connection config.
     * @return A new (or cached) {@link RedisAPI}
     */
    public static synchronized @NotNull RedisAPI getAPI(@NotNull RedisConf conf) {
        return acquire(conf, null);
    }

    /**
     * Get a {@link RedisAPI} instance for the given connection config.
     * @param logger a {@link LoggerService} to use for logging. Only used if this call is the one
     *               that creates the client; a cached client keeps the logger it was built with.
     * @return A new (or cached) {@link RedisAPI}
     */
    public static synchronized @NotNull RedisAPI getAPI(@NotNull RedisConf conf, @NotNull LoggerService logger) {
        return acquire(conf, logger);
    }

    private static @NotNull RedisAPI acquire(@NotNull RedisConf conf, @Nullable LoggerService logger) {
        RedisAPI api = apis.get(conf);
        if (api == null) {
            api = new RedisAPI(new RedisManager(conf, logger));
            apis.put(conf, api);
        }
        holders.merge(conf, 1, Integer::sum);
        return api;
    }

    /**
     * Releases one holder's claim on the client for this config, shutting it down once the last
     * holder has released it. Called by {@link RedisAPI#shutdown()}.
     */
    static synchronized void release(@NotNull RedisConf conf) {
        RedisAPI api = apis.get(conf);
        Integer count = holders.get(conf);
        if (api == null || count == null || count <= 0) {
            // Releasing something that was never acquired, or releasing twice. Doing nothing is
            // right, but staying silent about it is not: a double shutdown means the count is now
            // wrong for everyone else holding this connection.
            new RedisManagerWarning().warn(conf);
            return;
        }
        if (count > 1) {
            holders.put(conf, count - 1);
            return;
        }
        holders.remove(conf);
        apis.remove(conf);
        api.shutdownInternal();
    }

    /** Kept separate so the warning path names this class rather than the caller's logger. */
    private static class RedisManagerWarning {
        void warn(@NotNull RedisConf conf) {
            System.err.println(
                    "[KamiCommon] RedisAPI.shutdown() called for " + conf + " which has no active " +
                            "holder. This is a double shutdown, and the connection may already be closed."
            );
        }
    }
}

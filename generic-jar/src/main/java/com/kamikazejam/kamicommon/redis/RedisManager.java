package com.kamikazejam.kamicommon.redis;

import com.kamikazejam.kamicommon.redis.callback.RedisChannelCallback;
import com.kamikazejam.kamicommon.redis.logger.DefaultRedisLogger;
import com.kamikazejam.kamicommon.redis.util.RedisConf;
import com.kamikazejam.kamicommon.redis.util.RedisMonitor;
import com.kamikazejam.kamicommon.redis.util.RedisState;
import com.kamikazejam.kamicommon.util.JacksonUtil;
import com.kamikazejam.kamicommon.util.LoggerService;
import com.kamikazejam.kamicommon.util.interfaces.Service;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager for Redis connections and consumers (channel listeners)
 * Package-Private for use only by {@link RedisConnector} within {@link RedisAPI} instances.
 */
@Getter @SuppressWarnings("unused")
class RedisManager implements Service {
    private final @NotNull RedisState state = new RedisState();
    private final @NotNull RedisConf conf;
    private final @NotNull LoggerService logger;
    private boolean running = false;

    // Redis Connection Objects
    private RedisClient redisClient = null;
    private StatefulRedisConnection<String, String> redis = null;
    private StatefulRedisPubSubConnection<String, String> redisPubSub = null;
    private RedisMonitor redisMonitor = null;
    private final List<String> subscribedChannels = new ArrayList<>();

    RedisManager(@NotNull RedisConf conf, @Nullable LoggerService logger) {
        this.conf = conf;
        this.logger = (logger == null) ? new DefaultRedisLogger() : logger;
        this.connect();
    }

    // ------------------------------------------------- //
    //                      Service                      //
    // ------------------------------------------------- //
    @Override
    public boolean start() {
        logger.debug("Connecting to Redis");
        state.setEnabled(true);

        boolean redis = this.connect();
        this.running = true;

        if (!redis) {
            logger.error("Failed to start RedisManager, connection failed.");
            return false;
        }

        logger.debug("Connected to Redis");
        return true;
    }

    @Override
    public boolean shutdown() {
        state.setEnabled(false);

        // If not running, warn and return true (we are already shutdown)
        if (!running) {
            logger.warn("RedisManager.shutdown() called while service is not running!");
            return true;
        }

        // Unsubscribe from all channels
        if (reactive != null) {
            reactive.unsubscribe(subscribedChannels.toArray(new String[0])).subscribe();
        }
        reactive = null;
        subscribedChannels.clear();

        // Disconnect from Redis
        boolean redis = this.disconnect();
        this.running = false;

        if (!redis) {
            logger.error("Failed to shutdown RedisManager, disconnect failed.");
            return false;
        }

        logger.debug("Disconnected from Redis");
        return true;
    }



    // ------------------------------------------------- //
    //                 Redis Connection                  //
    // ------------------------------------------------- //
    public boolean connect() {
        try {
            state.setLastConnectionAttempt(System.currentTimeMillis());

            // Try connection
            if (redisClient == null) {
                redisClient = RedisClient.create(conf.getURI());
            }
            if (redis == null) {
                redis = redisClient.connect();
            }
            if (redisPubSub == null) {
                redisPubSub = redisClient.connectPubSub();
            }

            return true;
        } catch (Exception ex) {
            logger.info(ex, "Failed Redis connection attempt");
            return false;
        } finally {
            if (this.redisMonitor == null) {
                // Pass required objects to the monitor (since this class is package-private)
                this.redisMonitor = new RedisMonitor(this.state, this.logger, this::getRedis, this::connect);
            }
            if (!this.redisMonitor.isRunning()) {
                // Always true, no need to check
                this.redisMonitor.start();
            }
        }
    }

    private boolean disconnect() {
        if (this.redisMonitor != null) {
            this.redisMonitor.shutdown();
            this.redisMonitor = null;
        }

        if (this.redis != null && this.redis.isOpen()) {
            this.redis.close();
        }
        this.redis = null;

        if (this.redisClient != null) {
            this.redisClient.shutdown();
            this.redisClient = null;
        }
        return true;
    }


    // ------------------------------------------------- //
    //               RedisManager Methods                //
    // ------------------------------------------------- //
    private RedisPubSubReactiveCommands<String, String> reactive = null;
    <T> boolean subscribe(@NotNull RedisChannelCallback<T> callback, @NotNull Class<T> clazz, @NotNull String... channels) {
        try {
            logger.info("Subscribing to channels: " + String.join(", ", channels));

            // Ensure we have a valid reactive connection
            if (reactive == null) {
                reactive = redisPubSub.reactive();
            }
            final List<String> channelList = List.of(channels);

            // Subscribe to the channel
            reactive.subscribe(channels).subscribe();
            // Register an Observer
            reactive.observeChannels()
                    // Listen to our channels only
                    .filter(pm -> channelList.contains(pm.getChannel()))
                    // Deserialize the message and call the callback
                    .doOnNext(pm -> {
                        try {
                            T message = JacksonUtil.deserialize(clazz, pm.getMessage());
                            callback.onMessage(pm.getChannel(), message);
                        }catch (Throwable t) {
                            logger.error(t, "DeserializationError - channel: " + pm.getChannel() + " message: " + pm.getMessage());
                        }
                    }).subscribe();

            subscribedChannels.addAll(channelList);
            return true;
        } catch (Exception ex) {
            logger.info(ex, "Error subscribing");
            return false;
        }
    }

    <T> void publish(@NotNull String channel, T message, boolean sync) {
        // Publish a message to the channel
        if (sync) {
            redis.sync().publish(channel, JacksonUtil.serialize(message));
        }else {
            redis.async().publish(channel, JacksonUtil.serialize(message));
        }
    }
}

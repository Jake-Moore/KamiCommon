package com.kamikazejam.kamicommon;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Getter
@SuppressWarnings("unused")
public abstract class StandaloneJedisHandler {

    public interface LogCallback {
        void info(String message);
    }

    private JedisPool jedisPool;
    private PubSubListener pubSubListener;
    Thread subscription;

    @Setter private boolean debug;
    private final LogCallback logger;
    public StandaloneJedisHandler(LogCallback logger) {
        this(logger, false);
    }
    public StandaloneJedisHandler(LogCallback logger, boolean debug) {
        this.logger = logger;
        this.debug = debug;
    }

    //Mandatory abstract method
    public abstract PubSubListener createPubSubListener();


    //Other methods, can override if necessary
    public void connectPool(String host, int port) {
        try {
            this.jedisPool = new JedisPool(host, port);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void connectPool(String host, int port, String pass) {
        try {
            GenericObjectPoolConfig<Jedis> cfg = new GenericObjectPoolConfig<>();
            this.jedisPool = new JedisPool(cfg, host, port, Protocol.DEFAULT_TIMEOUT, pass, Protocol.DEFAULT_DATABASE);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Boolean> subscribe(String... channels) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        subscription = new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                if (debug) { logger.info("Subscription to channels: " + Arrays.toString(channels) + " started"); }
                pubSubListener = createPubSubListener();
                jedis.subscribe(pubSubListener, channels);
                if (debug) { logger.info("Subscription to channels: " + Arrays.toString(channels) + " ended"); }
                future.complete(true);
            } catch (Exception e) {
                if (debug) { logger.info("Subscription to channels: " + Arrays.toString(channels) + " failed"); }
                if (debug) { e.printStackTrace(); }
                future.complete(false);
            }
        });
        subscription.start();
        return future;
    }

    public void unsubscribe() {
        if (pubSubListener != null) {
            pubSubListener.unsubscribe();
        }
        if (subscription != null) {
            subscription.interrupt();
        }
    }

    public void publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Mandatory to create because of createPubSubListener() method
    //  onMessage override also required
    @Getter
    public static abstract class PubSubListener extends JedisPubSub {
        private final Plugin plugin;
        public PubSubListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public abstract void onMessage(String channelIn, String jsonString);
    }
}

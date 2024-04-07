package com.kamikazejam.kamicommon.jedis;

import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public abstract class AbstractJedisHandler {
    // ------------------------------------------------------------------------------------- //
    //                                          API                                          //
    // ------------------------------------------------------------------------------------- //
    public interface LogCallback {
        void info(String message);
    }


    // ------------------------------------------------------------------------------------- //
    //                                    ABSTRACTION                                        //
    // ------------------------------------------------------------------------------------- //
    public abstract PubSubListener createPubSubListener();



    // ------------------------------------------------------------------------------------- //
    //                                    CONSTRUCTORS                                       //
    // ------------------------------------------------------------------------------------- //
    private JedisPool jedisPool;
    private PubSubListener pubSubListener;
    Thread subscription;

    @Setter private boolean debug;
    private final LogCallback logger;
    public AbstractJedisHandler(LogCallback logger) {
        this(logger, false);
    }
    public AbstractJedisHandler(LogCallback logger, boolean debug) {
        this.logger = logger;
        this.debug = debug;
    }

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
}

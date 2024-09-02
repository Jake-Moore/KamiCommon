package com.kamikazejam.kamicommon.amqp;

import java.util.HashMap;
import java.util.Map;

/**
 * The source for all RabbitMQ interactions.
 * Use {@link #getAPI(String)} to fetch a {@link RabbitMQAPI} instance.
 * All instances are cached by URL for connection/channel reuse.
 */
@SuppressWarnings("unused")
public class RabbitMQConnector {
    // Handle a cache for multiple possible RabbitMQ connections
    private static final Map<String, RabbitMQManager> managers = new HashMap<>();
    private static RabbitMQManager getManager(String url) {
        if (!managers.containsKey(url)) {
            managers.put(url, new RabbitMQManager(url));
        }
        return managers.get(url);
    }

    // Handle a cache for RabbitMQAPI instances
    private static final Map<String, RabbitMQAPI> apis = new HashMap<>();

    /**
     * Get a {@link RabbitMQAPI} instance for the given connection URL.
     * @return A new (or cached) {@link RabbitMQAPI}
     */
    public static RabbitMQAPI getAPI(String url) {
        if (!apis.containsKey(url)) {
            apis.put(url, new RabbitMQAPI(getManager(url)));
        }
        return apis.get(url);
    }
}

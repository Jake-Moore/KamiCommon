package com.kamikazejam.kamicommon.redis.util;

import io.lettuce.core.RedisURI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class RedisConf {
    private final @NotNull String address;
    private final int port;
    private final @Nullable String password;

    /**
     * Creates a RedisConfig object with the given address, port, and password
     * @param password Nullable (for no authentication)
     */
    public RedisConf(@NotNull String address, int port, @Nullable String password) {
        this.address = address;
        this.port = port;
        this.password = password;
    }

    public @NotNull RedisURI getURI() {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(address)
                .withPort(port)
                .withSsl(false);
        if (password != null) {
            builder.withPassword((CharSequence) password);
        }
        return builder.build();
    }

    /**
     * Creates a RedisConfig object with the given address
     * Defaults: port 6379, no authentication
     */
    public static @NotNull RedisConf of(@NotNull String address) {
        return new RedisConf(address, 6379, null);
    }
    /**
     * Creates a RedisConfig object with the given address and port
     * Defaults: no authentication
     */
    public static @NotNull RedisConf of(@NotNull String address, int port) {
        return new RedisConf(address, port, null);
    }
    /**
     * Creates a RedisConfig object with the given address, port, and password
     */
    public static @NotNull RedisConf of(@NotNull String address, int port, @NotNull String password) {
        return new RedisConf(address, port, password);
    }
}

package com.kamikazejam.kamicommon.redis.util;

import io.lettuce.core.RedisURI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Connection settings for a single Redis server.
 *
 * <p>Redis 6 introduced ACL users, so a connection is identified by a username as well as a
 * password. A configuration that supplies only a password authenticates as {@code default}, which is
 * the pre-ACL behaviour and remains supported.
 *
 * <p>Instances are value objects and are used as cache keys by
 * {@link com.kamikazejam.kamicommon.redis.RedisConnector}, so two configurations describing the same
 * connection share one client.
 */
@Getter
public class RedisConf {
    /** Redis' own default port, used whenever a port is not given. */
    public static final int DEFAULT_PORT = 6379;

    private final @NotNull String address;
    private final int port;
    /** ACL username, or null to authenticate as {@code default}. */
    private final @Nullable String username;
    private final @Nullable String password;
    /** Logical database index, 0 unless a connection URL selected another. */
    private final int database;
    /** True for {@code rediss://}, meaning the connection is TLS. */
    private final boolean ssl;

    /**
     * Creates a config with the given address, port, and password, authenticating as {@code default}.
     * @param password Nullable (for no authentication)
     */
    public RedisConf(@NotNull String address, int port, @Nullable String password) {
        this(address, port, null, password, 0, false);
    }

    /**
     * Creates a config with the given address, port, username, and password.
     * @param username Nullable (to authenticate as {@code default})
     * @param password Nullable (for no authentication)
     */
    public RedisConf(@NotNull String address, int port, @Nullable String username, @Nullable String password) {
        this(address, port, username, password, 0, false);
    }

    /**
     * Creates a config with every connection setting stated.
     * @param username Nullable (to authenticate as {@code default})
     * @param password Nullable (for no authentication)
     * @param database Logical database index
     * @param ssl Whether to connect over TLS
     */
    public RedisConf(
            @NotNull String address,
            int port,
            @Nullable String username,
            @Nullable String password,
            int database,
            boolean ssl
    ) {
        this.address = address;
        this.port = port;
        this.username = emptyToNull(username);
        this.password = emptyToNull(password);
        this.database = database;
        this.ssl = ssl;
    }

    public @NotNull RedisURI getURI() {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(address)
                .withPort(port)
                .withDatabase(database)
                .withSsl(ssl);
        if (username != null) {
            // A username with no password is passed through as an empty password rather than
            // dropped. Dropping it would silently authenticate as `default`, which is the exact
            // mistake ACLs exist to prevent. An empty password succeeds for a `nopass` ACL user and
            // is rejected loudly by Redis for any other, which is the right way round.
            builder.withAuthentication(username, password == null ? new char[0] : password.toCharArray());
        } else if (password != null) {
            builder.withPassword((CharSequence) password);
        }
        return builder.build();
    }

    /**
     * Creates a RedisConfig object with the given address
     * Defaults: port 6379, no authentication
     */
    public static @NotNull RedisConf of(@NotNull String address) {
        return new RedisConf(address, DEFAULT_PORT, null, null);
    }
    /**
     * Creates a RedisConfig object with the given address and port
     * Defaults: no authentication
     */
    public static @NotNull RedisConf of(@NotNull String address, int port) {
        return new RedisConf(address, port, null, null);
    }
    /**
     * Creates a RedisConfig object with the given address, port, and password
     * Defaults: authenticates as {@code default}
     */
    public static @NotNull RedisConf of(@NotNull String address, int port, @NotNull String password) {
        return new RedisConf(address, port, null, password);
    }
    /**
     * Creates a RedisConfig object with the given address, port, username, and password
     */
    public static @NotNull RedisConf of(
            @NotNull String address,
            int port,
            @Nullable String username,
            @NotNull String password
    ) {
        return new RedisConf(address, port, username, password);
    }

    /**
     * Parses a Redis connection URL.
     *
     * <p>Accepted forms, where everything after the host is optional:
     * <pre>
     *   redis://host
     *   redis://host:6380
     *   redis://:password&#64;host
     *   redis://username:password&#64;host:6380/3
     *   rediss://username:password&#64;host      (TLS)
     * </pre>
     *
     * <p><b>Credentials must be written with a colon.</b> {@code redis://value@host} is rejected
     * rather than guessed at. Lettuce reads a colon-less userInfo as the <i>password</i>, so
     * {@code redis://myuser@host} against a server with {@code requirepass} set would connect
     * successfully as {@code default} while the caller believed they were authenticating as
     * {@code myuser}. That failure is silent and is the one this whole ACL arrangement exists to
     * prevent, so the ambiguous form is an error. Write {@code redis://myuser:secret@host} for an
     * ACL user, or {@code redis://:secret@host} for a password alone.
     *
     * @param url the connection URL
     * @return the parsed configuration
     * @throws IllegalArgumentException if the URL is not a Redis URL this can represent. The message
     *         never contains the URL itself, because it would carry the password into the log.
     */
    public static @NotNull RedisConf fromUrl(@NotNull String url) {
        URI uri;
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException ex) {
            // Deliberately not including the URL or the parser's own message: both quote the input,
            // and the input holds the password.
            throw new IllegalArgumentException("Redis connection URL is not a valid URI");
        }

        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase();
        boolean ssl;
        switch (scheme) {
            case "redis":
                ssl = false;
                break;
            case "rediss":
                ssl = true;
                break;
            default:
                // Naming the scheme is safe; it carries no secret. Sentinel and unix-socket URLs
                // parse cleanly and would otherwise be reduced to a host and port that point
                // somewhere else entirely.
                throw new IllegalArgumentException(
                        "Redis connection URL scheme must be redis:// or rediss://, got '" + scheme + "'"
                );
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Redis connection URL has no host");
        }
        int port = uri.getPort() == -1 ? DEFAULT_PORT : uri.getPort();

        int database = 0;
        String path = uri.getPath();
        if (path != null && !path.isEmpty() && !path.equals("/")) {
            String db = path.startsWith("/") ? path.substring(1) : path;
            try {
                database = Integer.parseInt(db);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Redis connection URL path must be a database index, got '" + db + "'"
                );
            }
            if (database < 0) {
                throw new IllegalArgumentException("Redis connection URL database index cannot be negative");
            }
        }

        // Query parameters are rejected rather than dropped. A URL carrying ?timeout= would
        // otherwise appear to take effect and do nothing.
        if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
            throw new IllegalArgumentException(
                    "Redis connection URL query parameters are not supported, so they would be " +
                            "silently ignored. Remove them and set the equivalent in code."
            );
        }

        String username = null;
        String password = null;
        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isEmpty()) {
            int colon = userInfo.indexOf(':');
            if (colon < 0) {
                throw new IllegalArgumentException(
                        "Redis connection URL credentials must be written as 'username:password@' or " +
                                "':password@'. A single value before '@' is ambiguous, and reading it as " +
                                "a password would authenticate as the default user without saying so."
                );
            }
            username = emptyToNull(userInfo.substring(0, colon));
            password = emptyToNull(userInfo.substring(colon + 1));
        }

        return new RedisConf(host, port, username, password, database, ssl);
    }

    private static @Nullable String emptyToNull(@Nullable String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    // ------------------------------------------------- //
    //                  Value semantics                  //
    // ------------------------------------------------- //
    // RedisConnector keys two HashMaps on this type to reuse one client per connection. Without
    // these it inherited identity equality, so every lookup missed and every call built another
    // lettuce client with its own connections and event loop threads. The reuse the connector
    // documents has never actually happened.
    //
    // Every field participates. Two configs that differ only by username are different identities
    // on the server and must not share a connection.
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof RedisConf)) { return false; }
        RedisConf that = (RedisConf) o;
        return port == that.port
                && database == that.database
                && ssl == that.ssl
                && address.equals(that.address)
                && Objects.equals(username, that.username)
                && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port, username, password, database, ssl);
    }

    /** Never includes the password. This type is a natural thing to put in a log line. */
    @Override
    public String toString() {
        return "RedisConf{" + (ssl ? "rediss" : "redis") + "://"
                + (username == null ? "" : username + "@")
                + address + ":" + port + "/" + database
                + (password == null ? "" : " (password set)") + "}";
    }
}

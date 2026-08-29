package com.kamikazejam.kamicommon.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public abstract class Database implements DatabaseListener {
    private @Nullable HikariDataSource datasource;
    private final @NotNull String database;

    private static final String RELOCATED_DRIVER =
            "com.kamikazejam.kamicommon.mysql.cj.jdbc.MysqlDataSource";
    private static final String FALLBACK_DRIVER =
            "com.mysql.cj.jdbc.MysqlDataSource";

    public Database(
            @NotNull String address,
            int port,
            @NotNull String database,
            @NotNull String user,
            @NotNull String pass
    ) {
        requireJava11();
        this.database = database;

        HikariConfig hikari = new HikariConfig();
        hikari.setDataSourceClassName(detectDataSourceClassName());

        hikari.addDataSourceProperty("serverName", address);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", pass);

        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "256");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.datasource = new HikariDataSource(hikari);
        this.onConnected();
    }

    /**
     * Shuts down the database connection pool.
     */
    public void shutdown() {
        HikariDataSource source = this.datasource;
        if (source != null && !source.isClosed()) {
            source.close();
        }
        this.datasource = null;
    }

    private static String detectDataSourceClassName() {
        // Try the relocated driver first
        if (isClassAvailable(RELOCATED_DRIVER)) {
            return RELOCATED_DRIVER;
        }
        // Fall back to the standard driver
        return FALLBACK_DRIVER;
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @NotNull
    public Connection getConnection() {
        HikariDataSource datasource = this.datasource;
        if (datasource == null) {
            throw new IllegalStateException("Database connection pool is not initialized.");
        }

        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            this.onExceptionCaught(e);
            throw new RuntimeException("Failed to get database connection.", e);
        }
    }

    @Override
    public final void onConnected() {
        info("Database Setup for " + database + " Complete!");
    }

    @Override
    public final void onConnectionFailed() {
        warn("Could not connect to Database: " + database);
    }

    @Override
    public final void onExceptionCaught(Exception exception) {
        warn("An error occurred while setting up Database: " + database);
    }

    public abstract void info(String msg);
    public abstract void warn(String msg);

    /**
     * The one place KamiCommon needs more than Java 8.
     * <p>
     * Everything else in this library runs on Java 8 so that 1.8.x servers can load it. HikariCP is
     * the exception: it is the only bundled library whose bytecode is above Java 8, at class-file
     * major 55, and this class is the only one that touches it. So the floor split is not "KamiCommon
     * needs Java 11", it is "the database support does", and this is where somebody meets that.
     * </p>
     * <p>
     * Without this check the first thing a Java 8 server sees is an {@link UnsupportedClassVersionError}
     * naming {@code com.kamikazejam.kamicommon.hikari.HikariConfig}, a relocated class that appears in
     * no documentation and matches nothing searchable. It is thrown from the class loader rather than
     * from library code, so it does not even point at the plugin that caused it.
     * </p>
     *
     * @throws IllegalStateException if this server runs a JVM older than 11
     */
    private static void requireJava11() {
        int major = majorJavaVersion();
        if (major >= 11 || major == UNKNOWN_JAVA_VERSION) { return; }
        throw new IllegalStateException(
                "KamiCommon's database support requires Java 11 or newer, and this server is running"
                        + " Java " + System.getProperty("java.specification.version") + "."
                        + " The rest of KamiCommon runs on Java 8; only this part does not, because the"
                        + " connection pool it uses (HikariCP) is Java 11. Either run the server on"
                        + " Java 11+ or do not use com.kamikazejam.kamicommon.database."
        );
    }

    /** Sentinel for a {@code java.specification.version} this code cannot parse. */
    private static final int UNKNOWN_JAVA_VERSION = -1;

    /**
     * @return the major Java version, or {@link #UNKNOWN_JAVA_VERSION} if it cannot be read
     */
    private static int majorJavaVersion() {
        String spec = System.getProperty("java.specification.version");
        if (spec == null || spec.isEmpty()) { return UNKNOWN_JAVA_VERSION; }
        // "1.8" through Java 8, then "9", "11", "17" and so on.
        if (spec.startsWith("1.")) { spec = spec.substring(2); }
        int dot = spec.indexOf('.');
        if (dot >= 0) { spec = spec.substring(0, dot); }
        try {
            return Integer.parseInt(spec);
        } catch (NumberFormatException e) {
            // An unreadable version must not stop a server that would otherwise work. The guard is
            // here to replace an unhelpful error with a helpful one, not to add a new way to fail.
            return UNKNOWN_JAVA_VERSION;
        }
    }
}

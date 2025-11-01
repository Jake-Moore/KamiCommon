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
        var source = this.datasource;
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
        var datasource = this.datasource;
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
}
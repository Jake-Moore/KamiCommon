package com.kamikazejam.kamicommon.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public abstract class Database implements DatabaseListener {
    private final HikariDataSource datasource;
    private final String database;

    private static final String RELOCATED_DRIVER =
            "com.kamikazejam.kamicommon.mysql.cj.jdbc.MysqlDataSource";
    private static final String FALLBACK_DRIVER =
            "com.mysql.cj.jdbc.MysqlDataSource";

    public Database(String address, int port, String database, String user, String pass) {
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

        hikari.addDataSourceProperty("useSSL", false);
        hikari.addDataSourceProperty("verifyServerCertificate", false);
        hikari.addDataSourceProperty("useUnicode", true);
        hikari.addDataSourceProperty("characterEncoding", "utf8");

        this.datasource = new HikariDataSource(hikari);
        this.onConnected();
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

    public Connection getConnection() {
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            this.onExceptionCaught(e);
        }
        return null;
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
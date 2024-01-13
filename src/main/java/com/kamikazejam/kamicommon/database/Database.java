package com.kamikazejam.kamicommon.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class Database implements DatabaseListener {
    private final HikariDataSource datasource;
    private final String database;

    public Database(String address, int port, String database, String user, String pass) {
        this.database = database;

        HikariConfig hikari = new HikariConfig();
        // Use this method so that maven minimize-jar keeps these classes
        hikari.setDataSource(new MysqlDataSource());

        hikari.addDataSourceProperty("serverName", address);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", pass);

        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "256");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        Map<String, Object> properties = new HashMap<>();
        properties.put("useSSL", false);
        properties.put("verifyServerCertificate", false);
        properties.put("useUnicode", true);
        properties.put("characterEncoding", "utf8");

        String propertiesString = properties.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(";"));

        this.datasource = new HikariDataSource(hikari);
        this.onConnected();

        hikari.addDataSourceProperty("properties", propertiesString);
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

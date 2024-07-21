package com.kamikazejam.kamicommon.util.log;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@SuppressWarnings("unused")
public abstract class LoggerService {

    // Abstraction
    public abstract String getLoggerName();
    public abstract boolean isDebug();

    // Public Methods
    public void info(@NotNull String msg) {
        logToConsole(msg, Level.INFO);
    }

    public void info(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.INFO);
        throwable.printStackTrace();
    }

    public void info(@NotNull Throwable throwable, @NotNull String msg) {
        logToConsole(msg + " - " + throwable.getMessage(), Level.INFO);
        throwable.printStackTrace();
    }

    public void debug(@NotNull String msg) {
        if (!isDebug()) {
            return;
        }
        logToConsole(msg, Level.FINE);
    }

    public void warn(@NotNull String msg) {
        logToConsole(msg, Level.WARNING);
    }

    public void warn(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.WARNING);
        throwable.printStackTrace();
    }

    public void warn(@NotNull Throwable throwable, @NotNull String msg) {
        logToConsole(msg + " - " + throwable.getMessage(), Level.WARNING);
        throwable.printStackTrace();
    }

    public void warning(@NotNull String msg) {
        this.warn(msg);
    }

    public void warning(@NotNull Throwable throwable) {
        this.warn(throwable);
    }

    public void warning(@NotNull Throwable throwable, @NotNull String msg) {
        this.warn(throwable, msg);
    }

    public void severe(@NotNull String msg) {
        logToConsole(msg, Level.SEVERE);
    }

    public void severe(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.SEVERE);
        throwable.printStackTrace();
    }

    public void severe(@NotNull Throwable throwable, @NotNull String msg) {
        logToConsole(msg + " - " + throwable.getMessage(), Level.SEVERE);
        throwable.printStackTrace();
    }

    public void error(@NotNull String msg) {
        this.severe(msg);
    }

    public void error(@NotNull Throwable throwable) {
        this.severe(throwable);
    }

    public void error(@NotNull Throwable throwable, @NotNull String msg) {
        this.severe(throwable, msg);
    }

    public void logToConsole(String a, Level level) {
        System.out.println("[" + level.getName() + "] [KamiCommon] [" + getLoggerName() + "] " + a);
    }
}

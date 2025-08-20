package com.kamikazejam.kamicommon.util.log;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@SuppressWarnings("unused")
public abstract class LoggerService {

    // Abstraction
    public abstract String getLoggerName();
    public abstract boolean isDebug();

    // Public Methods
    public void info(@NotNull String message) {
        logToConsole(message, Level.INFO);
    }

    public void info(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.INFO);
        throwable.printStackTrace();
    }

    public void info(@NotNull Throwable throwable, @NotNull String message) {
        logToConsole(message + " - " + throwable.getMessage(), Level.INFO);
        throwable.printStackTrace();
    }

    public void debug(@NotNull String message) {
        if (!isDebug()) {
            return;
        }
        logToConsole(message, Level.FINE);
    }

    public void warn(@NotNull String message) {
        logToConsole(message, Level.WARNING);
    }

    public void warn(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.WARNING);
        throwable.printStackTrace();
    }

    public void warn(@NotNull Throwable throwable, @NotNull String message) {
        logToConsole(message + " - " + throwable.getMessage(), Level.WARNING);
        throwable.printStackTrace();
    }

    public void warning(@NotNull String message) {
        this.warn(message);
    }

    public void warning(@NotNull Throwable throwable) {
        this.warn(throwable);
    }

    public void warning(@NotNull Throwable throwable, @NotNull String message) {
        this.warn(throwable, message);
    }

    public void severe(@NotNull String message) {
        logToConsole(message, Level.SEVERE);
    }

    public void severe(@NotNull Throwable throwable) {
        logToConsole(throwable.getMessage(), Level.SEVERE);
        throwable.printStackTrace();
    }

    public void severe(@NotNull Throwable throwable, @NotNull String message) {
        logToConsole(message + " - " + throwable.getMessage(), Level.SEVERE);
        throwable.printStackTrace();
    }

    public void error(@NotNull String message) {
        this.severe(message);
    }

    public void error(@NotNull Throwable throwable) {
        this.severe(throwable);
    }

    public void error(@NotNull Throwable throwable, @NotNull String message) {
        this.severe(throwable, message);
    }

    public void logToConsole(String message, Level level) {
        System.out.println("[" + level.getName() + "] [KamiCommon] [" + getLoggerName() + "] " + message);
    }
}

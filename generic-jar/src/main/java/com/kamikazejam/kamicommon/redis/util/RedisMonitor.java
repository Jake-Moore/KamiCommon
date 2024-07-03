package com.kamikazejam.kamicommon.redis.util;

import com.kamikazejam.kamicommon.util.LoggerService;
import com.kamikazejam.kamicommon.util.interfaces.Service;
import io.lettuce.core.api.StatefulRedisConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class RedisMonitor extends TimerTask implements Service {
    public interface RedisAccess {
        StatefulRedisConnection<String, String> getRedis();
    }
    public interface RedisRetry {
        void connect();
    }

    private Timer timer = null;
    private TimerTask timerTask = null;
    private final @NotNull RedisState state;
    private final @NotNull LoggerService logger;
    private final @NotNull RedisAccess access;
    private final @NotNull RedisRetry retry;

    public RedisMonitor(@NotNull RedisState state, @NotNull LoggerService logger, @NotNull RedisAccess access, @NotNull RedisRetry retry) {
        this.state = state;
        this.logger = logger;
        this.access = access;
        this.retry = retry;
    }

    @Override
    public boolean start() {
        if (this.timer == null) {
            this.timer = new Timer();
            this.timerTask = this;
            this.timer.scheduleAtFixedRate(this.timerTask, 1000, 1000);
        } else {
            throw new IllegalStateException("Redis Monitor is already running: cannot start");
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        // Cancel the timer
        if (this.timer != null) {
            if (this.timerTask != null) {
                this.timerTask.cancel();
            }

            this.timer.cancel();
            this.timer.purge();
        } else {
            throw new IllegalStateException("Redis Monitor is not running: cannot stop");
        }
        return true;
    }

    @Override
    public boolean isRunning() {
        return this.timer != null;
    }

    @Override
    public void run() {
        // Check if connection is alive
        if (access.getRedis() == null) return;

        try {
            String reply = access.getRedis().sync().ping();

            if (reply.contains("PONG")) {
                this.handleConnected();
            } else {
                this.handleDisconnected();
                logger.warning("Non-PONG ping reply in Redis Monitor: " + reply);
            }
        } catch (Exception ex) {
            // Ignore errors (like disconnect, if we are not enabled)
            if (!state.isEnabled()) {
                return;
            }

            // Failed, assume disconnected
            this.handleDisconnected();
            logger.severe("Error in Redis Monitor task:");
            ex.printStackTrace();
        }
    }

    private void handleConnected() {
        if (!state.isConnected()) {
            state.setConnected(true);
            if (!state.isInitConnect()) {
                state.setInitConnect(true);
                logger.info("Redis initial connection succeeded");
            } else {
                logger.info("Redis connection restored");
            }
        }
    }

    private void handleDisconnected() {
        if (!state.isEnabled()) {
            return;
        }
        if (state.isConnected()) {
            state.setConnected(false);
            logger.info("Redis connection lost");
        }
        // Does this do anything??
        retry.connect();
    }
}

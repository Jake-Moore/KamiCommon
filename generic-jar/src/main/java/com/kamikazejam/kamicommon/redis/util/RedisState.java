package com.kamikazejam.kamicommon.redis.util;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RedisState {
    private boolean enabled = true;
    private boolean initConnect = false;
    private boolean connected = false;
    private volatile long lastConnectionAttempt = 0;
}

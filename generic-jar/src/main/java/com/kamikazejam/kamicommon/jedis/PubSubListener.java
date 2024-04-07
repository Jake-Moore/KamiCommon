package com.kamikazejam.kamicommon.jedis;

import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

/**
 * Required by {@link AbstractJedisHandler#createPubSubListener()}
 */
@Getter
public abstract class PubSubListener extends JedisPubSub {

    public PubSubListener() {}

    @Override
    public abstract void onMessage(String channelIn, String jsonString);
}

package com.kamikazejam.kamicommon.amqp;

import com.kamikazejam.kamicommon.amqp.data.RabbitRpcQueue;

@SuppressWarnings("unused")
public class RabbitQueues {
    // Stores static instances of RabbitRpcQueue for commonality between client and server programs
    public static final RabbitRpcQueue getTexturesUrl = new RabbitRpcQueue("getTexturesUrl");

}

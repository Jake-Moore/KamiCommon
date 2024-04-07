package com.kamikazejam.kamicommon.jedis;

@SuppressWarnings("unused")
public abstract class StandaloneJedisHandler extends AbstractJedisHandler {
    // Default Constructor: Use System.out.println as logger
    public StandaloneJedisHandler() {
        super(System.out::println);
    }
}

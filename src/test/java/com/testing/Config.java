package com.testing;

import com.kamikazejamplugins.kamicommon.configuration.config.StandaloneConfig;
import com.kamikazejamplugins.kamicommon.util.MessageBuilder;

import java.io.File;
import java.util.Arrays;

public class Config {

    public static void main(String[] args) {
        StandaloneConfig config = new StandaloneConfig(new File("C:\\Users\\Jake\\Desktop\\config.yml"));
        config.set("testDouble", -3D);
        config.addDefault("testList2", Arrays.asList("test1", "test2", "test3"));
        config.save();
        config.reload();
        System.out.println(config.getDouble("testDouble"));



        System.out.println(MessageBuilder.of(config, "testList2").getLines());
    }
}
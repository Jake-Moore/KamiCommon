package com.kamikazejamplugins.kamicommon.config.testing;

import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import com.kamikazejamplugins.kamicommon.util.MessageBuilder;

import java.io.File;
import java.util.Arrays;

public class Config extends KamiConfig {
    public Config(Object plugin, File file) {
        super(plugin, file, true);
    }

    public static void main(String[] args) {
        Config config = new Config(null, new File("C:\\Users\\Jake\\Desktop\\config.yml"));
        config.set("testDouble", -3D);
        config.addDefault("testList2", Arrays.asList("test1", "test2", "test3"));
        config.save();
        config.reload();
        System.out.println(config.getDouble("testDouble"));


        System.out.println(MessageBuilder.of(config, "testList2").getLines());
    }
}
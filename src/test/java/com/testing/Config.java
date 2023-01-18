package com.testing;

import com.kamikazejamplugins.kamicommon.configuration.config.StandaloneConfig;

import java.io.File;

public class Config {

    public static void main(String[] args) {
        StandaloneConfig config = new StandaloneConfig(new File("C:\\Users\\Jake\\Desktop\\config.yml"), true);
        config.save();
        config.reload();

        System.out.println("Keys: " + config.getConfigurationSection("levels").getKeys(false));
    }
}
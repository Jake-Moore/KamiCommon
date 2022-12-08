package com.kamikazejamplugins.kamicommon.config.testing;

import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;

import java.io.File;

public class FreeItems extends KamiConfig {
    public FreeItems(File file) {
        super(null, file, true);
    }

    public static void main(String[] args) {
        new FreeItems(new File("C:\\Users\\Jake\\Desktop\\freeItems.yml"));
    }
}

package com.kamikazejamplugins.kamicommon.config.data;

import lombok.Getter;

public class ConfigComment {
    @Getter private final String key;
    @Getter private final String comment;
    @Getter private final boolean above;

    /**
     * Internal Class for the Parser, do not use :)
     */
    public ConfigComment(String key, String comment, boolean above) {
        this.key = key;
        this.comment = comment;
        this.above = above;
    }
}

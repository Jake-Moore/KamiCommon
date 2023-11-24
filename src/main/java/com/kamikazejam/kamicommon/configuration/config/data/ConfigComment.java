package com.kamikazejam.kamicommon.configuration.config.data;

import lombok.Getter;

import java.util.List;

/**
 * This object contains the data for an AbstractConfig yaml comment.
 */
@Getter
public class ConfigComment {
    private final String key;
    private final List<String> comment;
    private final boolean above;

    /**
     * Internal Class for the Parser, do not use :)
     */
    public ConfigComment(String key, List<String> comment, boolean above) {
        this.key = key;
        this.comment = comment;
        this.above = above;
    }
}

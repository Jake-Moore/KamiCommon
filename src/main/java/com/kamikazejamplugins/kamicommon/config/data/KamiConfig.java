package com.kamikazejamplugins.kamicommon.config.data;

import lombok.Getter;

import java.io.File;

@SuppressWarnings("unused")
public abstract class KamiConfig {
    @Getter private final File file;

    /**
     * The base for a config file. Extend this class and supply a file, then call:
     * <b>AnnotationParser.parse(config...);</b> for all instances of this class.
     * @param file The java File where the .yml file is located.
     */
    public KamiConfig(File file) {
        this.file = file;
    }
}

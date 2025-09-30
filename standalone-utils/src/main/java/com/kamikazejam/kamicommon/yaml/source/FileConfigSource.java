package com.kamikazejam.kamicommon.yaml.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Optional;

public class FileConfigSource implements ConfigSource {
    private final File file;

    public FileConfigSource(@NotNull File file) {
        this.file = file;
    }

    @Override public @NotNull String id() {return file.getAbsolutePath();}

    @Override
    public @NotNull Optional<InputStream> openStream() throws IOException {
        if (!file.exists()) return Optional.empty();
        return Optional.of(new BufferedInputStream(new FileInputStream(file)));
    }

    @Override public boolean isWritable() {return true;}

    @Override
    public boolean ensureExistsIfWritable() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create parent dirs for: " + file.getAbsolutePath());
        }
        if (!file.exists()) {
            return file.createNewFile();
        }
        return true;
    }

    @Override
    public boolean write(byte[] data, @NotNull Charset charset) throws IOException {
        ensureExistsIfWritable();
        Files.write(file.toPath(), data);
        return true;
    }

    @Override
    public @Nullable File asFileIfPresent() {return file;}

    @Override
    public @NotNull String getResourceStreamPath() {
        // Default behavior = look for a resource with the same name as the file in the root of the jar
        return file.getName();
    }
}

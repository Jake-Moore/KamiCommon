package com.kamikazejam.kamicommon.yaml.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * A {@link ConfigSource} implementation that reads from a predefined yaml string in memory.<br>
 * <br>
 * Supports ONLY read operations. Writes unsupported as the source is an immutable string.<br>
 * Does NOT support defaults via a resource stream.<br>
 * <br>
 * Source string can be any string parsable by snake-yaml, like the contents of a yaml file.
 */
@SuppressWarnings("unused")
public class StringConfigSource implements ConfigSource {
    private final @NotNull String content;
    private final @Nullable String id;

    /**
     * Construct a config source with the provided yaml content string.<br>
     * See {@link StringConfigSource(String, String)} for adding a custom ID for logging/debugging.
     */
    public StringConfigSource(@NotNull String content) {
        this(content, null);
    }

    public StringConfigSource(@NotNull String content, @Nullable String id) {
        this.content = content;
        this.id = id;
    }

    @Override
    public @NotNull String id() {
        return id != null ? id : "StringConfigSource";
    }

    @Override
    public @NotNull Optional<InputStream> openStream() {
        return Optional.of(
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
        );
    }

    @Override
    public @Nullable String getResourceStreamPath() {
        return null;
    }
}

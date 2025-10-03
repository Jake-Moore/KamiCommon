package com.kamikazejam.kamicommon.yaml.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Represents a source of configuration data that can provide an {@link InputStream} for reading<br>
 * and may optionally support writing/persistence.<br>
 * <br>
 * Typical implementations:<br>
 * - File-backed source (writable)<br>
 * - Provider/remote/in-memory source (read-only)<br>
 * <br>
 * Contract notes:<br>
 * - Each call to {@link #openStream()} should return a fresh stream positioned at the start.<br>
 * - If {@link #isWritable()} is false, {@link #write(byte[], Charset)} must throw {@link UnsupportedOperationException}.<br>
 * - {@link #ensureExistsIfWritable()} should create any required backing structures for writable sources.<br>
 * - {@link #asFileIfPresent()} returns a {@link File} only for file-backed sources; otherwise null.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface ConfigSource {
    /**
     * A human-readable identifier for this source (e.g., absolute file path, URL, logical name).<br>
     * Uniqueness is not required and the value is intended primarily for logging/debugging.
     *
     * @return identifier string for this source (not necessarily unique)
     */
    @NotNull String id();

    /**
     * Opens a fresh InputStream for the current contents of this source.<br>
     * The caller is responsible for closing the returned stream.
     *
     * @return an Optional containing a new InputStream if content is available; empty if absent
     * @throws IOException if the stream cannot be opened due to I/O errors
     */
    @NotNull Optional<InputStream> openStream() throws IOException;

    /**
     * Indicates whether this source supports persisting data via write(...).
     *
     * @return true if writable; false if read-only
     */
    default boolean isWritable() { return false; }

    /**
     * Ensures the writable source has any required backing structures (e.g., parent directories/files).<br>
     * No-op for read-only sources.
     *
     * @return true if the source exists or was created successfully; false otherwise
     * @throws IOException if creation fails due to I/O errors
     */
    default boolean ensureExistsIfWritable() throws IOException { return true; }

    /**
     * Writes serialized data to this source using the provided charset.<br>
     * Implementations should overwrite the existing contents atomically when possible.<br>
     * <br>
     * For read-only sources, this method must throw UnsupportedOperationException.
     *
     * @param data bytes to persist
     * @param charset character set used to interpret textual data (if relevant)
     * @return true if the data was persisted successfully; false otherwise
     * @throws IOException if an I/O error occurs while writing
     * @throws UnsupportedOperationException if the source is not writable
     */
    default boolean write(byte[] data, @NotNull Charset charset) throws IOException {
        throw new UnsupportedOperationException("Read-only source: " + id());
    }

    /**
     * Returns the underlying File if this source is file-backed; otherwise null.<br>
     * Useful for compatibility with APIs that require a File.
     *
     * @return File when applicable; null for non-file-backed sources
     */
    @Nullable
    default File asFileIfPresent() { return null; }

    /**
     * Returns the path to a resource stream for default configuration data, if available.<br>
     * This is typically used to load default settings bundled with the application/plugin.<br>
     * <br>
     * If no resource stream is associated, may return null.
     */
    @Nullable
    String getResourceStreamPath();
}

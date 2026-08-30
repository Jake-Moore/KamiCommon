package com.kamikazejam.kamicommon.yaml;

import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import com.kamikazejam.kamicommon.yaml.util.YamlDefaultsUtil;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class AbstractYamlHandler<T extends AbstractYamlConfiguration> {
    public final @NotNull AbstractConfig<?> abstractConfig;
    public final @NotNull ConfigSource source;
    public final @Nullable Supplier<InputStream> defaultsStream;
    // Should not be null after loadConfig() is called
    public @Nullable T config;

    /**
     * @param abstractConfig The parent config instance who holds this handler.
     * @param source The source of the configuration file (yaml content).
     * @param defaultsStream An optional stream (of a YAML config) to read default values from, can be null.
     */
    public AbstractYamlHandler(
            @NotNull AbstractConfig<?> abstractConfig,
            @NotNull ConfigSource source,
            @Nullable Supplier<InputStream> defaultsStream
    ) {
        this.abstractConfig = abstractConfig;
        this.source = source;
        this.defaultsStream = defaultsStream;
        this.config = null;
    }

    public abstract @NotNull T newConfig(@NotNull MappingNode node, @NotNull ConfigSource source);

    public abstract @NotNull MemorySectionMethods<?> newMemorySection(@NotNull MappingNode node);

    public abstract void error(String s);

    public abstract void warn(String s);

    @NotNull
    public T loadConfig() {
        try {
            // If writable file source, ensure file exists (and that dirs exist)
            if (source.isWritable()) {
                try {
                    source.ensureExistsIfWritable();
                } catch (IOException e) {
                    error("Could not prepare writable source (" + source.id() + "): " + e.getMessage());
                }
            }

            Optional<InputStream> opt = source.openStream();
            MappingNode rootNode;

            // Parse the content into a yaml mapping node
            if (opt.isPresent()) {
                try (Reader reader = new InputStreamReader(opt.get(), StandardCharsets.UTF_8)) {
                    // Require that the yaml root is parsed as a map
                    rootNode = (MappingNode) YamlUtil.getYaml().compose(reader);
                }
            } else {
                // No data available from source, create empty root node
                rootNode = createNewMappingNode();
            }

            // Create the new config object
            @NotNull T config = newConfig(rootNode, source);

            // Add defaults if a stream was provided
            if (defaultsStream != null) {
                config = YamlDefaultsUtil.addDefaults(this, defaultsStream, config);
            }

            // Persist only if the source is writable (i.e. file-based)
            if (source.isWritable()) {
                save();
            }

            return this.config = config;
        }catch (IOException e) {
            e.printStackTrace();
            // Fallback to empty config on error
            return newConfig(createNewMappingNode(), source);
        }
    }

    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    @Internal
    public boolean save() {
        // Require a valid config and writable source
        if (config == null) return false;
        if (!source.isWritable()) return false;
        return config.save();
    }

    @NotNull
    public static MappingNode createNewMappingNode() {
        return new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
    }
}

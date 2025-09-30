package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.InputStream;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class YamlHandler extends AbstractYamlHandler<YamlConfiguration> {
    @NotNull private final LoggerService logger;

    /**
     * @param abstractConfig The parent config instance who holds this handler.
     * @param logger The logger service for warnings and errors.
     * @param source The source of the configuration file (yaml content).
     * @param defaultsStream An optional stream (of a YAML config) to read default values from, can be null.
     */
    public YamlHandler(
            @NotNull AbstractConfig<?> abstractConfig,
            @NotNull LoggerService logger,
            @NotNull ConfigSource source,
            @Nullable Supplier<InputStream> defaultsStream
    ) {
        super(abstractConfig, source, defaultsStream);
        this.logger = logger;
    }

    @Override
    public void error(String s) {
        logger.severe(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    @Override
    public @NotNull YamlConfiguration newConfig(@NotNull MappingNode node, @NotNull ConfigSource source) {
        return new YamlConfiguration(node, source);
    }

    @Override
    public @NotNull MemorySectionMethods<?> newMemorySection(@NotNull MappingNode node) {
        return new MemorySection(node, "", null);
    }
}

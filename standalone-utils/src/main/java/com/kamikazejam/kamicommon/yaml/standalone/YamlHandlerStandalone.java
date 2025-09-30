package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import com.kamikazejam.kamicommon.util.data.ANSI;
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
public class YamlHandlerStandalone extends AbstractYamlHandler<YamlConfigurationStandalone> {

    /**
     * @param abstractConfig The parent config instance who holds this handler.
     * @param logger The logger service for warnings and errors.
     * @param source The source of the configuration file (yaml content).
     * @param defaultsStream An optional stream (of a YAML config) to read default values from, can be null.
     */
    public YamlHandlerStandalone(
            @NotNull AbstractConfig<?> abstractConfig,
            @NotNull LoggerService logger,
            @NotNull ConfigSource source,
            @Nullable Supplier<InputStream> defaultsStream
    ) {
        super(abstractConfig, source, defaultsStream);
    }

    @Override
    public void error(String s) {
        System.out.println(ANSI.RED + "[KamiCommon] " + s);
    }

    @Override
    public void warn(String s) {
        System.out.println(ANSI.YELLOW + "[KamiCommon] " + s);
    }

    @Override
    public @NotNull YamlConfigurationStandalone newConfig(@NotNull MappingNode node, @NotNull ConfigSource source) {
        return new YamlConfigurationStandalone(node, source);
    }

    @Override
    public @NotNull MemorySectionMethods<?> newMemorySection(@NotNull MappingNode node) {
        return new MemorySectionStandalone(node, "", null);
    }
}

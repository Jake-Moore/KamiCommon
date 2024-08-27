package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;

@Getter
@SuppressWarnings("unused")
public class MemorySectionStandalone extends MemorySectionMethods<MemorySectionStandalone> implements ConfigurationSectionStandalone {
    @Getter(AccessLevel.NONE)
    private final @NotNull String fullPath;
    public MemorySectionStandalone(@Nullable MappingNode node, @NotNull String fullPath) {
        super(node);
        this.fullPath = fullPath;
    }

    @Override
    public @NotNull MemorySectionStandalone getConfigurationSection(String key) {
        Object o = get(key);
        String newPath = (this.fullPath.isEmpty()) ? key : this.fullPath + "." + key;
        if (o instanceof MappingNode m) {
            return new MemorySectionStandalone(m, newPath);
        }
        return new MemorySectionStandalone(AbstractYamlHandler.createNewMappingNode(), newPath);
    }

    @Override
    public String getCurrentPath() {
        return fullPath;
    }
}

package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.ConfigurationMethods;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

@Getter
@SuppressWarnings("unused")
public class MemorySectionStandalone extends MemorySectionMethods<MemorySectionStandalone> implements ConfigurationSectionStandalone {
    @Getter(AccessLevel.NONE)
    private final @NotNull String fullPath;
    public MemorySectionStandalone(@Nullable MappingNode node, @NotNull String fullPath, @Nullable ConfigurationMethods<?> parent) {
        super(node, parent);
        this.fullPath = fullPath;
    }

    @Override
    public @NotNull MemorySectionStandalone getConfigurationSection(String key) {
        Object o = get(key);
        String newPath = (this.fullPath.isEmpty()) ? key : this.fullPath + "." + key;
        if (o instanceof MappingNode m) {
            return new MemorySectionStandalone(m, newPath, this);
        }
        return new MemorySectionStandalone(AbstractYamlHandler.createNewMappingNode(), newPath, this);
    }

    @Override
    public @NotNull ConfigurationSequenceStandalone getConfigurationSequence(String key) {
        @Nullable Node node = getNode(key);
        String newPath = (this.fullPath.isEmpty()) ? key : this.fullPath + "." + key;

        if (node instanceof SequenceNode sequenceNode) {
            return new ConfigurationSequenceStandalone(this, sequenceNode, newPath);
        }

        // Return empty sequence if not found or not a sequence
        return new ConfigurationSequenceStandalone(this, null, newPath);
    }

    @Override
    public String getCurrentPath() {
        return fullPath;
    }
}

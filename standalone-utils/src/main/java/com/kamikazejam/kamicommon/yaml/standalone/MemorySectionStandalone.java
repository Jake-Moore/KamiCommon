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

/**
 * A nested section of a {@link com.kamikazejam.kamicommon.configuration.standalone.StandaloneConfig
 * StandaloneConfig}, exposing the same read and write surface as the config itself over one branch of the
 * document. {@link #getConfigurationSection(String)} never returns {@code null}: a key that is missing, or
 * that holds something other than a mapping, yields a section backed by a fresh detached node, so reads
 * fall through to their defaults but writes to that section never reach the parent document.
 *
 * @see <a href="https://github.com/Jake-Moore/KamiCommon/wiki/v5-Config-System">Configuration System (wiki)</a>
 */
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
        if (o instanceof MappingNode) {
            MappingNode m = (MappingNode) o;
            return new MemorySectionStandalone(m, newPath, this);
        }
        return new MemorySectionStandalone(AbstractYamlHandler.createNewMappingNode(), newPath, this);
    }

    @Override
    public @NotNull ConfigurationSequenceStandalone getConfigurationSequence(String key) {
        @Nullable Node node = getNode(key);
        String newPath = (this.fullPath.isEmpty()) ? key : this.fullPath + "." + key;

        if (node instanceof SequenceNode) {
            SequenceNode sequenceNode = (SequenceNode) node;
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

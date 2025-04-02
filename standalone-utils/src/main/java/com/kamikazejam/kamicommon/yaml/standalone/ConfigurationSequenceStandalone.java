package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.base.ConfigurationSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationSequenceStandalone extends ConfigurationSequence<ConfigurationSectionStandalone> {
    public ConfigurationSequenceStandalone(ConfigurationSectionStandalone parent, @Nullable SequenceNode node, String newPath) {
        super(parent, node, newPath);
    }

    @Override
    protected @NotNull List<ConfigurationSectionStandalone> loadSections(@Nullable SequenceNode sequenceNode, String newPath) {
        if (sequenceNode == null) return List.of();
        List<ConfigurationSectionStandalone> sections = new ArrayList<>();

        for (Node node : sequenceNode.getValue()) {
            if (node instanceof MappingNode mappingNode) {
                sections.add(new MemorySectionStandalone(mappingNode, newPath, this.parent));
            } else {
                throw new IllegalStateException("Sequence contains non-mapping element at path: " + newPath);
            }
        }

        return sections;
    }
}

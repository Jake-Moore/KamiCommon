package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;

@Getter
@SuppressWarnings("unused")
public class MemorySectionStandalone extends MemorySectionMethods<MemorySectionStandalone> implements ConfigurationSectionStandalone {
    public MemorySectionStandalone(@Nullable MappingNode node) {
        super(node);
    }

    @Override
    public @NotNull MemorySectionStandalone getConfigurationSection(String key) {
        Object o = get(key);
        if (o instanceof MappingNode m) {
            return new MemorySectionStandalone(m);
        }
        return new MemorySectionStandalone(AbstractYamlHandler.createNewMappingNode());
    }





}

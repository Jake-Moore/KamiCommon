package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;

@Getter
@SuppressWarnings("unused")
public class MemorySectionStandalone extends MemorySectionMethods<MemorySectionStandalone> implements ConfigurationSectionStandalone {
    public MemorySectionStandalone(@Nullable MappingNode node) {
        super(node);
    }

    @Override
    public MemorySectionStandalone getConfigurationSection(String key) {
        Object o = get(key);
        if (o instanceof MappingNode) {
            MappingNode m = (MappingNode) o;
            return new MemorySectionStandalone(m);
        }
        return new MemorySectionStandalone(AbstractYamlHandler.createNewMappingNode());
    }





}

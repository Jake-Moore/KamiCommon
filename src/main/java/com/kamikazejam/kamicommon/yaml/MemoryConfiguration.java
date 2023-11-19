package com.kamikazejam.kamicommon.yaml;

import org.yaml.snakeyaml.nodes.MappingNode;

@SuppressWarnings("unused")
public class MemoryConfiguration extends MemorySection {
    public MemoryConfiguration(MappingNode node) {
        super(node);
    }
}
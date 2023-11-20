package com.kamikazejam.kamicommon.yaml.data;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class NodePair {
    public final String key;
    public final ScalarNode scalarNode;
    public final boolean terminatesInValue;
    public NodePair(String key, ScalarNode scalarNode, boolean terminatesInValue) {
        this.key = key;
        this.scalarNode = scalarNode;
        this.terminatesInValue = terminatesInValue;
    }
}

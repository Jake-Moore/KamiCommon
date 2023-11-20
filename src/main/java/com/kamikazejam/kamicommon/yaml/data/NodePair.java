package com.kamikazejam.kamicommon.yaml.data;

import org.yaml.snakeyaml.nodes.Node;

public class NodePair {
    public final String key;
    public final Node node;
    public NodePair(String key, Node node) {
        this.key = key;
        this.node = node;
    }
}

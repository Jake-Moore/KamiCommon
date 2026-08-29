package com.kamikazejam.kamicommon.yaml.util;

import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.Objects;

/**
 * Was a record. This module targets Java 8 so that a 1.8.x server can load it, and records are
 * Java 16, so the accessors, equality and {@code toString} are spelled out here instead. The
 * accessor names keep the record's shape ({@code key()}, not {@code getKey()}), so callers are
 * unchanged.
 */
public final class NodePair {
    private final String key;
    private final ScalarNode scalarNode;
    private final boolean terminatesInValue;

    public NodePair(String key, ScalarNode scalarNode, boolean terminatesInValue) {
        this.key = key;
        this.scalarNode = scalarNode;
        this.terminatesInValue = terminatesInValue;
    }

    public String key() { return key; }

    public ScalarNode scalarNode() { return scalarNode; }

    public boolean terminatesInValue() { return terminatesInValue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof NodePair)) { return false; }
        NodePair other = (NodePair) o;
        return terminatesInValue == other.terminatesInValue
                && Objects.equals(key, other.key)
                && Objects.equals(scalarNode, other.scalarNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, scalarNode, terminatesInValue);
    }

    @Override
    public String toString() {
        return "NodePair[key=" + key + ", scalarNode=" + scalarNode
                + ", terminatesInValue=" + terminatesInValue + "]";
    }
}

package com.kamikazejam.kamicommon.yaml;

import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

import java.util.Set;

public interface AbstractYamlConfiguration {
    boolean save();
    boolean save(boolean force);
    boolean contains(String key);
    Set<String> getKeys(boolean deep);
    NodeTuple getNodeTuple(String key);
    Node getKeyNode(String key);
    @SuppressWarnings("UnusedReturnValue")
    @Nullable NodeTuple internalPut(String key, Object value);
}

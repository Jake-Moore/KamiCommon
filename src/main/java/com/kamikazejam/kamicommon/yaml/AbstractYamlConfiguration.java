package com.kamikazejam.kamicommon.yaml;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

import javax.annotation.Nullable;
import java.util.Set;

public interface AbstractYamlConfiguration {
    boolean save();
    boolean contains(String key);
    Set<String> getKeys(boolean deep);
    NodeTuple getNodeTuple(String key);
    Node getKeyNode(String key);
    @SuppressWarnings("UnusedReturnValue")
    @Nullable NodeTuple internalPut(String key, Object value);
}

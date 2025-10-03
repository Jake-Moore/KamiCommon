package com.kamikazejam.kamicommon.yaml.util;

import org.yaml.snakeyaml.nodes.ScalarNode;

public record NodePair(String key, ScalarNode scalarNode, boolean terminatesInValue) {}

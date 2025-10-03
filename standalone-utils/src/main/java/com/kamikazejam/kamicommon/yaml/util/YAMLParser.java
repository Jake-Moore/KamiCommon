package com.kamikazejam.kamicommon.yaml.util;

import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class YAMLParser {

    public static Pair<List<String>, List<NodePair>> parseOrderedKeys(InputStream stream) {
        List<String> valueKeys = new ArrayList<>();
        List<NodePair> allKeyNodes = new ArrayList<>();

        Node rootNode = YamlUtil.getYaml().compose(new InputStreamReader(stream, StandardCharsets.UTF_8));
        if (rootNode instanceof MappingNode) {
            processYAMLNode(rootNode, valueKeys, allKeyNodes, "");
        }

        return Pair.of(valueKeys, allKeyNodes);
    }

    public static List<NodePair> parseOrderedNodes(InputStream stream) {
        List<NodePair> allKeyNodes = new ArrayList<>();

        Node rootNode = YamlUtil.getYaml().compose(new InputStreamReader(stream, StandardCharsets.UTF_8));
        if (rootNode instanceof MappingNode) {
            processYAMLNode(rootNode, null, allKeyNodes, "");
        }

        return allKeyNodes;
    }

    private static void processYAMLNode(Node node, @Nullable List<String> keysWithValues, List<NodePair> allKeyNodes, String parentKey) {
        if (node instanceof MappingNode mappingNode) {

            for (int i = 0; i < mappingNode.getValue().size(); i++) {
                NodeTuple tuple = mappingNode.getValue().get(i);
                ScalarNode keyNode = (ScalarNode) tuple.getKeyNode();
                Node valueNode = tuple.getValueNode();

                String currentKey = parentKey.isEmpty() ? keyNode.getValue() : parentKey + "." + keyNode.getValue();

                // Add only keys that terminate in a value
                boolean terminates = (!(valueNode instanceof MappingNode) && keysWithValues != null);
                if (terminates) {
                    keysWithValues.add(currentKey);
                }

                allKeyNodes.add(new NodePair(currentKey, keyNode, terminates));
                processYAMLNode(valueNode, keysWithValues, allKeyNodes, currentKey);
            }
        }
    }
}

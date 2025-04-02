package com.kamikazejam.kamicommon.yaml;

import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.base.ConfigurationMethods;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@SuppressWarnings("unused")
public abstract class AbstractMemorySection<T extends AbstractMemorySection<?>> {
    private boolean changed = false;

    private final @NotNull MappingNode node;
    private final @Nullable ConfigurationMethods<?> parent;
    public AbstractMemorySection(@Nullable MappingNode node, @Nullable ConfigurationMethods<?> parent) {
        if (node == null) { node = AbstractYamlHandler.createNewMappingNode(); }
        this.node = node;
        this.parent = parent;
    }

    public void set(String key, Object value) { put(key, value); }
    public void put(String key, Object value) {
        if (value == null) { put(node, key, null); return; }
        put(node, key, value);
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        if (parent != null && parent.isChanged() != changed) {
            parent.setChanged(changed);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public @Nullable NodeTuple internalPut(String key, @Nullable Object value) {
        return put(node, key, value);
    }

    private @Nullable NodeTuple put(MappingNode node, String key, @Nullable Object value) {
        if (node == null) { return null; }

        // Get the current key
        String[] keys = key.split("\\.");
        if (keys.length == 0) { return null; }

        // Keep track of if we have changed the config
        if (!this.isChanged()) { this.setChanged(true); }

        // If we have hit the end of the key and should insert the value here
        if (keys.length == 1) {
            String part = keys[0];
            // Try to fetch a TupleNode with this key
            NodeTuple tuple = null;
            ScalarNode scalarKeyNode = null;
            for (NodeTuple t : node.getValue()) {
                Node keyNode = t.getKeyNode();
                if (keyNode instanceof ScalarNode scalarNode) {
                    if (scalarNode.getValue().equals(part)) {
                        tuple = t;
                        scalarKeyNode = scalarNode;
                        break;
                    }
                }
            }
            // Save the comments
            List<CommentLine> blockComments = null;
            List<CommentLine> inlineComments = null;
            List<CommentLine> endComments = null;
            if (scalarKeyNode != null) {
                blockComments = scalarKeyNode.getBlockComments();
                inlineComments = scalarKeyNode.getInLineComments();
                endComments = scalarKeyNode.getEndComments();
            }

            // Remove this node since we can't edit it
            node.getValue().remove(tuple);

            if (value != null) {
                // Create a new tuple
                Node keyNode = getScalarNode(part, DumperOptions.ScalarStyle.PLAIN);
                keyNode.setBlockComments(blockComments); keyNode.setInLineComments(inlineComments); keyNode.setEndComments(endComments);
                Node valueNode = getValueNode(value);
                tuple = new NodeTuple(keyNode, valueNode);
                node.getValue().add(tuple);
                return tuple;
            }
            return null;
        }
        // > 1 parts left
        // We need to make sure there is a MappingNode
        String part = keys[0];
        NodeTuple tuple = null;
        for (NodeTuple t : node.getValue()) {
            Node keyNode = t.getKeyNode();
            if (keyNode instanceof ScalarNode scalarNode) {
                if (scalarNode.getValue().equals(part)) {
                    tuple = t;
                    break;
                }
            }
        }

        // Create a new Tuple for this
        if (tuple == null) {
            return insertNewTuple(node, key, value, part);
        }

        // We need to preserve the data of this tuple
        Node valueNode = tuple.getValueNode();
        if (valueNode instanceof MappingNode) {
            return put((MappingNode) valueNode, key.substring(part.length() + 1), value);
        }else {
            node.getValue().remove(tuple);
            return insertNewTuple(node, key, value, part);
        }
    }

    private NodeTuple insertNewTuple(MappingNode node, String key, @Nullable Object value, String part) {
        Node keyNode = getScalarNode(part, DumperOptions.ScalarStyle.PLAIN);
        MappingNode valueNode = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
        NodeTuple tuple = new NodeTuple(keyNode, valueNode);
        node.getValue().add(tuple);
        put(valueNode, key.substring(part.length() + 1), value);
        return tuple;
    }

    private ScalarNode getScalarNode(String value, DumperOptions.ScalarStyle style) {
        return new ScalarNode(Tag.STR, value, null, null, style);
    }

    private Node getValueNode(Object value) {
        // Lists are set as SequenceNodes
        if (value instanceof List<?>) {
            List<Node> list = new ArrayList<>();
            for (Object object : (List<?>) value) {
                list.add(getScalarNode(object.toString(), DumperOptions.ScalarStyle.DOUBLE_QUOTED));
            }
            return new SequenceNode(Tag.SEQ, list, DumperOptions.FlowStyle.AUTO);
        }
        // Booleans need to be plain (sorta)
        if (isBooleanStr(value)) {
            return new ScalarNode(Tag.BOOL, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
        }
        // Numbers also need to be different
        if (parseBigDecimal(value.toString()) != null) {
            if (value.toString().contains(".")) {
                return new ScalarNode(Tag.FLOAT, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
            }else {
                return new ScalarNode(Tag.INT, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
            }
        }

        return getScalarNode(value.toString(), DumperOptions.ScalarStyle.DOUBLE_QUOTED);
    }

    @Nullable
    protected BigDecimal parseBigDecimal(String s) {
        // If it's any of the following, remove the last character
        String l = s.toLowerCase();
        try { return new BigDecimal(s);
        }catch (Exception ignored) {}
        return null;
    }

    private boolean isBooleanStr(Object value) {
        String s = value.toString();
        if (s.equalsIgnoreCase("true")) { return true; }
        if (s.equalsIgnoreCase("false")) { return true; }
        if (s.equalsIgnoreCase("yes")) { return true; }
        if (s.equalsIgnoreCase("no")) { return true; }
        if (s.equalsIgnoreCase("on")) { return true; }
        return s.equalsIgnoreCase("off");
    }

    // Internal recursive method to get an object from a MappingNode
    @Nullable
    protected Object getObject(MappingNode node, String search) {
        @Nullable Node n = getNodeInternal(node, search, "");
        return getNodeValue(n);
    }

    public static @Nullable Object getNodeValue(@Nullable Node node) {
        if (node == null) { return null; }

        if (node instanceof ScalarNode) {
            return ((ScalarNode) node).getValue();
        }
        if (node instanceof MappingNode) {
            return node;
        }
        if (node instanceof SequenceNode) {
            return node;
        }

//        if (node instanceof SequenceNode sequenceNode) {
//            List<Object> valuesList = new ArrayList<>();
//            for (Node elementNode : sequenceNode.getValue()) {
//                Object value = getNodeValue(elementNode); // Recursive call
//                if (value != null) {
//                    valuesList.add(value);
//                }
//            }
//            return valuesList;
//        }
        throw new IllegalStateException(
                "Unknown node type (2): " + node.getNodeId() + " (" + node.getClass().getSimpleName() + ")"
        );
//        return node;
    }

    @Nullable Node getNode(String key) {
        return getNodeInternal(node, key, "");
    }

    // Internal recursive method to get an object from a MappingNode
    private @Nullable Node getNodeInternal(MappingNode node, String search, String currentKey) {
        List<NodeTuple> values = node.getValue();
        for (NodeTuple tuple : values) {
            // Skip non-scalar nodes (shouldn't happen) and nodes that don't match the search
            Pair<Node, String> pair = verifyNodeTuple(tuple, search, currentKey);
            if (pair == null) { continue; }
            Node valueNode = pair.getA();
            String key2 = pair.getB();

            // If this is a mapping node, continue (it can't be a value)
            if (valueNode instanceof MappingNode m) {
                if (key2.equals(search)) { return m; } // If the key is the search, return the valueNode

                Node n = getNodeInternal(m, search, key2);
                if (n != null) { return n; }
                continue;
            }

            if (valueNode instanceof ScalarNode s) {
                if (key2.equals(search)) { return s; }
            }else if (valueNode instanceof SequenceNode s) {
                if (!key2.equals(search)) { continue; }
                return s;
            } else {
                throw new RuntimeException("Cannot get string, unknown node type: " + valueNode.getNodeId());
            }
        }
        return null;
    }

    // Adds a key to the current key
    private static String concat(String key, String nextNode) {
        if (key.isEmpty()) {
            return nextNode;
        } else {
            return key + "." + nextNode;
        }
    }

    protected Set<String> getKeys(MappingNode node, boolean deep) {
        if (node == null) { return Collections.emptySet(); }
        if (!deep) { return getShallowKeys(node); }
        return getDeepKeys(node, "");
    }

    private Set<String> getShallowKeys(MappingNode node) {
        Set<String> keys = new HashSet<>();
        for (NodeTuple tuple : node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            if (keyNode instanceof ScalarNode) {
                keys.add(((ScalarNode) keyNode).getValue());
            }
        }
        return keys;
    }

    private Set<String> getDeepKeys(MappingNode node, String baseKey) {
        Set<String> keys = new HashSet<>();
        for (NodeTuple tuple : node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();
            if (keyNode instanceof ScalarNode) {
                if (valueNode instanceof MappingNode) {
                    keys.addAll(getDeepKeys((MappingNode) valueNode, concat(baseKey, ((ScalarNode) keyNode).getValue())));
                }else if (valueNode instanceof ScalarNode) {
                    keys.add(concat(baseKey, ((ScalarNode) keyNode).getValue()));
                }else if (valueNode instanceof SequenceNode) {
                    keys.add(concat(baseKey, ((ScalarNode) keyNode).getValue()));
                }
            } else if (keyNode instanceof MappingNode) {
                keys.addAll(getDeepKeys((MappingNode) keyNode, baseKey));
            }
        }
        return keys;
    }


    // Internal method to recursively search for a key
    protected boolean contains(MappingNode node, String key, String currentKey) {
        for (NodeTuple tuple : node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();

            if (keyNode instanceof ScalarNode) {
                String k = concat(currentKey, ((ScalarNode) keyNode).getValue());
                if (k.equals(key)) { return true; }
            }

            if (valueNode instanceof MappingNode) {
                if (keyNode instanceof ScalarNode) {
                    String concat = concat(currentKey, ((ScalarNode) keyNode).getValue());
                    if (!key.startsWith(concat)) { continue; }
                    if (contains((MappingNode) valueNode, key, concat)) { return true; }
                }
            }
        }

        return false;
    }

    protected boolean isPrimitiveWrapper(final Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }

    @Nullable
    public Node getKeyNode(String key) {
        NodeTuple o = getNodeTuple(key);
        if (o != null) { return o.getKeyNode(); }
        return null;
    }

    @Nullable
    public NodeTuple getNodeTuple(String key) {
        return getNodeTupleInternal(node, key, "");
    }


    // Internal recursive method to get an object from a MappingNode
    private @Nullable NodeTuple getNodeTupleInternal(MappingNode node, String search, String currentKey) {
        List<NodeTuple> values = node.getValue();
        for (NodeTuple tuple : values) {
            // Skip non-scalar nodes (shouldn't happen) and nodes that don't match the search
            Pair<Node, String> pair = verifyNodeTuple(tuple, search, currentKey);
            if (pair == null) { continue; }
            Node valueNode = pair.getA();
            String key2 = pair.getB();

            // If this is a mapping node, continue (it can't be a value)
            if (valueNode instanceof MappingNode m) {
                if (key2.equals(search)) { return tuple; } // If the key is the search, return the valueNode

                NodeTuple o = getNodeTupleInternal(m, search, key2);
                if (o != null) { return o; }
                continue;
            }

            if (valueNode instanceof ScalarNode) {
                if (key2.equals(search)) { return tuple; }
            }else if (valueNode instanceof SequenceNode) {
                if (!key2.equals(search)) { continue; }
                return tuple;
            } else {
                throw new RuntimeException("Cannot get string, unknown node type: " + valueNode.getNodeId());
            }
        }
        return null;
    }

    private @Nullable Pair<Node, String> verifyNodeTuple(NodeTuple tuple, String search, String currentKey) {
        if (!(tuple.getKeyNode() instanceof ScalarNode scalarNode)) {
            throw new RuntimeException("getNodeInternal unknown node type: " + tuple.getKeyNode());
        }
        Node valueNode = tuple.getValueNode(); // Node that contains the next Node or the value object

        String key2 = concat(currentKey, scalarNode.getValue());

        // Optimize by pruning branches that don't match the search
        if (!search.startsWith(key2)) { return null; }
        return Pair.of(valueNode, key2);
    }
}

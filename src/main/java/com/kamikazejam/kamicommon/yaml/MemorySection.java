package com.kamikazejam.kamicommon.yaml;

import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.handler.AbstractYamlHandler;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class MemorySection extends ConfigurationSection {
    private final @Nonnull MappingNode node;
    public MemorySection(@Nullable MappingNode node) {
        if (node == null) { node = AbstractYamlHandler.createNewMappingNode(); }
        this.node = node;
    }

    @Override
    public void put(String key, Object value) {
        if (value == null) { put(node, key, null); return; }

        //ItemStacks are special
        if (getItemStackHelper() != null && getItemStackHelper().isStack(value)) {
            setItemStack(key, value); return;
        }
        if (getItemStackHelper() != null && getItemStackHelper().isBuilder(value)) {
            setItemBuilder(key, value); return;
        }

        put(node, key, value);
    }

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
                if (keyNode instanceof ScalarNode) {
                    ScalarNode scalarNode = (ScalarNode) keyNode;
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
            if (keyNode instanceof ScalarNode) {
                ScalarNode scalarNode = (ScalarNode) keyNode;
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
        if (getBigDecimal(value.toString()) != null) {
            if (value.toString().contains(".")) {
                return new ScalarNode(Tag.FLOAT, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
            }else {
                return new ScalarNode(Tag.INT, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
            }
        }

        return getScalarNode(value.toString(), DumperOptions.ScalarStyle.DOUBLE_QUOTED);
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

    @Override
    public Object get(String key) {
        return getObject(node, key);
    }

    // Internal recursive method to get an object from a MappingNode
    private @Nullable Object getObject(MappingNode node, String search) {
        Node n = getNodeInternal(node, search, "");
        return getNodeValue(n);
    }

    public static @Nullable Object getNodeValue(Node node) {
        if (node instanceof ScalarNode) {
            return ((ScalarNode) node).getValue();
        }
        if (node instanceof SequenceNode) {
            SequenceNode s = (SequenceNode) node;
            List<String> valuesList = new ArrayList<>();
            for (Node n2 : s.getValue()) {
                if (n2 instanceof ScalarNode) {
                    ScalarNode scalar = (ScalarNode) n2;
                    valuesList.add(scalar.getValue());
                }else { System.out.print("Unknown node type (2): " + n2.getNodeId()); }
            }
            return valuesList;
        }
        return node;
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
            if (valueNode instanceof MappingNode) {
                MappingNode m = (MappingNode) valueNode;
                if (key2.equals(search)) { return m; } // If the key is the search, return the valueNode

                Node n = getNodeInternal(m, search, key2);
                if (n != null) { return n; }
                continue;
            }

            if (valueNode instanceof ScalarNode) {
                ScalarNode s = (ScalarNode) valueNode;
                if (key2.equals(search)) { return s; }
            }else if (valueNode instanceof SequenceNode) {
                SequenceNode s = (SequenceNode) valueNode;
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

    @Override
    public Object get(String key, Object def) {
        if (contains(key)) { return get(key);
        }else { return def; }
    }

    @Override public void putString(String key, String value) { put(key, value); }
    @Override public void setString(String key, String value) { put(key, value); }

    @Override public void putBoolean(String key, boolean value) { put(key, value); }
    @Override public void setBoolean(String key, boolean value) { put(key, value); }

    @Override public void putInteger(String key, int value) { put(key, value); }
    @Override public void putInt(String key, int value) { put(key, value); }

    @Override public void setInteger(String key, int value) { put(key, value); }
    @Override public void setInt(String key, int value) { put(key, value); }


    @Override public void putLong(String key, long value) { put(key, value); }
    @Override public void setLong(String key, long value) { put(key, value); }

    @Override public void putDouble(String key, double value) { put(key, value); }
    @Override public void setDouble(String key, double value) { put(key, value); }

    @Override public void putFloat(String key, float value) { put(key, value); }
    @Override public void setFloat(String key, float value) { put(key, value); }

    @Override public void putByte(String key, byte value) { put(key, value); }
    @Override public void setByte(String key, byte value) { put(key, value); }

    @Override public void putShort(String key, short value) { put(key, value); }
    @Override public void setShort(String key, short value) { put(key, value); }

    @Override
    public MemoryConfiguration getConfigurationSection(String key) {
        Object o = get(key);
        if (o instanceof MappingNode) {
            MappingNode m = (MappingNode) o;
            return new MemoryConfiguration(m);
        }
        return new MemoryConfiguration(AbstractYamlHandler.createNewMappingNode());
    }

    @Override
    public String getString(String key) { return getString(key, null); }
    @Override
    public String getString(String key, String def) {
        Object val = get(key, def);
        return (val != null) ? val.toString() : def;
    }
    @Override
    public boolean isString(String key) { return get(key) instanceof String; }


    @Override
    public boolean getBoolean(String key) { return getBoolean(key, false); }
    @Override
    public boolean getBoolean(String key, boolean def) {
        Object val = get(key, def);
        if (val == null) { return def; }
        if (val instanceof Boolean) { return (boolean) val; }
        if (val instanceof String) {
            String s = (String) val;
            if (s.equalsIgnoreCase("true")) { return true; }
            if (s.equalsIgnoreCase("false")) { return false; }
            if (s.equalsIgnoreCase("yes")) { return true; }
            if (s.equalsIgnoreCase("no")) { return false; }
            if (s.equalsIgnoreCase("on")) { return true; }
            if (s.equalsIgnoreCase("off")) { return false; }
        }
        return def;
    }
    @Override
    public boolean isBoolean(String key) { return get(key) instanceof Boolean; }





    @Override
    public byte getByte(String key) { return getByte(key, (byte) 0); }
    @Override
    public byte getByte(String key, byte def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isByte(key)) { return def; }
        return bd.byteValue();
    }
    @Override
    public boolean isByte(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Byte.MAX_VALUE && bd.doubleValue() >= Byte.MIN_VALUE);
    }



    @Override
    public short getShort(String key) { return getShort(key, (short) 0); }
    @Override
    public short getShort(String key, short def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isShort(key)) { return def; }
        return bd.shortValue();
    }
    @Override
    public boolean isShort(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Short.MAX_VALUE && bd.doubleValue() >= Short.MIN_VALUE);
    }



    @Override
    public int getInt(String key) { return getInt(key, 0); }
    @Override
    public int getInt(String key, int def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isInt(key)) { return def; }
        return bd.intValue();
    }
    @Override
    public boolean isInt(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Integer.MAX_VALUE && bd.doubleValue() >= Integer.MIN_VALUE);
    }



    @Override
    public long getLong(String key) { return getLong(key, 0L); }
    @Override
    public long getLong(String key, long def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isLong(key)) { return def; }
        return bd.longValue();
    }
    @Override
    public boolean isLong(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Long.MAX_VALUE && bd.doubleValue() >= Long.MIN_VALUE);
    }



    @Override
    public float getFloat(String key) { return getFloat(key, 0f); }
    @Override
    public float getFloat(String key, float def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isFloat(key)) { return def; }
        return bd.floatValue();
    }
    @Override
    public boolean isFloat(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (Math.abs(bd.doubleValue()) <= Float.MAX_VALUE && Math.abs(bd.doubleValue()) >= Float.MIN_VALUE);
    }



    @Override
    public double getDouble(String key) { return getDouble(key, 0.0); }
    @Override
    public double getDouble(String key, double def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isDouble(key)) { return def; }
        return bd.doubleValue();
    }
    @Override
    public boolean isDouble(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (Math.abs(bd.doubleValue()) <= Double.MAX_VALUE && Math.abs(bd.doubleValue()) >= Double.MIN_VALUE);
    }



    private @Nullable BigDecimal getNumberAt(String key) {
        String s = getString(key);
        if (s == null) { return null; }

        return getBigDecimal(s);
    }
    private @Nullable BigDecimal getBigDecimal(String s) {
        // If it's any of the following, remove the last character
        String l = s.toLowerCase();
        try { return new BigDecimal(s);
        }catch (Exception ignored) {}
        return null;
    }

    @Override
    public List<?> getList(String key) { return getList(key, null); }
    @Override
    public List<?> getList(String key, final List<?> def) {
        Object val = get(key, def);
        return (List<?>)((val instanceof List) ? val : def);
    }
    @Override
    public boolean isList(String key) { return get(key) instanceof List; }

    @Override
    public List<String> getStringList(String key) { return getStringList(key, new ArrayList<>()); }
    @Override
    public List<String> getStringList(String key, List<String> def) {
        final List<?> list = getList(key);
        if (list == null) { return def; }

        final List<String> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof String || this.isPrimitiveWrapper(object)) {
                result.add(String.valueOf(object));
            }
        }
        return result;
    }




    @Override
    public List<Integer> getIntegerList(String key) {
        return getIntegerList(key, new ArrayList<>());
    }
    @Override
    public List<Integer> getIntegerList(String key, List<Integer> def) {
        List<?> list = getList(key);
        if (list == null) { return def; }

        final List<Integer> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer)object);
            }
            else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String)object));
                } catch (Exception ignored) {}
            }
            else if (object instanceof Character) {
                result.add((int)(char)object);
            }
            else {
                if (!(object instanceof Number)) { continue; }
                result.add(((Number)object).intValue());
            }
        }
        return result;
    }



    @Override
    public List<Byte> getByteList(String key) {
        return getByteList(key, new ArrayList<>());
    }
    @Override
    public List<Byte> getByteList(String key, List<Byte> def) {
        List<?> list = getList(key);
        if (list == null) { return def; }

        final List<Byte> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof Integer) {
                if (((Integer)object) >= -128 && ((Integer)object) <= 127) {
                    result.add(((Integer)object).byteValue());
                }
            }
            else if (object instanceof String || object instanceof Character) {
                try {
                    Byte.valueOf(object.toString());
                } catch (Exception ignored) {}
            }
            else {
                if (!(object instanceof Number)) { continue; }
                int i = ((Number)object).intValue();
                if (i >= -128 && i <= 127) {
                    result.add(((Number)object).byteValue());
                }
            }
        }
        return result;
    }

    /**
     * Returns the keys of the config
     * If Deep is enabled, it will dig and find all valid keys that resolve to a value
     * @param deep Whether to search for all sub-keys
     * @return The list of keys found
     */
    @Override
    public Set<String> getKeys(boolean deep) {
        return getKeys(node, deep);
    }

    private Set<String> getKeys(MappingNode node, boolean deep) {
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





    @Override
    public boolean isConfigurationSection(final String key) {
        return get(key) instanceof MappingNode;
    }

    @Override
    public boolean contains(String key) {
        return contains(node, key, "");
    }

    // Internal method to recursively search for a key
    private boolean contains(MappingNode node, String key, String currentKey) {
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


    @Override
    public boolean isSet(String key) { return contains(key); }

    @Override
    public void addDefault(String key, Object o) {
        if (contains(key)) { return; }
        set(key, o);
    }

    protected boolean isPrimitiveWrapper(final Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }

    @Override
    public boolean isEmpty() {
        if (node.getValue() == null) { return true; }
        return node.getValue().isEmpty();
    }

    /**
     * Supported in Spigot-Backed Config classes, you must cast to ItemStack if return is not null.
     * @return the ItemStack at the given key, or null if it doesn't exist
     */
    @Override
    public Object getItemStack(String key) {
        if (getItemStackHelper() == null) { return null; }
        return getItemStackHelper().getItemStack(key);
    }

    @Override
    public Object getItemStack(String key, Object def) {
        if (getItemStackHelper() == null) { return def; }
        return getItemStackHelper().getItemStack(key, def);
    }

    @Override
    public void setItemStack(String key, Object item) {
        if (getItemStackHelper() == null) { return; }
        getItemStackHelper().setItemStack(key, item);
    }

    @Override
    public void setItemBuilder(String key, Object builder) {
        if (getItemStackHelper() == null) { return; }
        getItemStackHelper().setItemBuilder(key, builder);
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
            if (valueNode instanceof MappingNode) {
                MappingNode m = (MappingNode) valueNode;
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
        if (!(tuple.getKeyNode() instanceof ScalarNode)) {
            throw new RuntimeException("getNodeInternal unknown node type: " + tuple.getKeyNode());
        }
        Node valueNode = tuple.getValueNode(); // Node that contains the next Node or the value object

        ScalarNode scalarNode = (ScalarNode) tuple.getKeyNode();
        String key2 = concat(currentKey, scalarNode.getValue());

        // Optimize by pruning branches that don't match the search
        if (!search.startsWith(key2)) { return null; }
        return Pair.of(valueNode, key2);
    }
}

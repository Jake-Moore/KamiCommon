package com.kamikazejam.kamicommon.yaml.handler;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.configuration.config.AbstractConfig;
import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejam.kamicommon.yaml.MemorySection;
import com.kamikazejam.kamicommon.yaml.YamlConfiguration;
import com.kamikazejam.kamicommon.yaml.data.NodePair;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.*;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class AbstractYamlHandler {
    protected final File configFile;
    protected final String fileName;
    protected YamlConfiguration config;
    protected final AbstractConfig abstractConfig;

    public AbstractYamlHandler(AbstractConfig abstractConfig, File configFile) {
        this.abstractConfig = abstractConfig;
        this.configFile = configFile;
        this.fileName = configFile.getName();
        this.config = null;
    }

    public AbstractYamlHandler(AbstractConfig abstractConfig, File configFile, String fileName) {
        this.abstractConfig = abstractConfig;
        this.configFile = configFile;
        this.fileName = fileName;
        this.config = null;
    }

    public YamlConfiguration loadConfig(boolean addDefaults) {
        return loadConfig(addDefaults, null);
    }

    public YamlConfiguration loadConfig(boolean addDefaults, @Nullable Supplier<InputStream> stream) {
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    if (!configFile.getParentFile().mkdirs()) {
                        error("Could not create config file dirs for (" + configFile.getAbsolutePath() + "), stopping");
                    }
                }
                if (!configFile.createNewFile()) {
                    error("Could not create config file, stopping");
                    System.exit(0);
                }
            }

            Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8);
            config = new YamlConfiguration((MappingNode) KamiCommon.getYaml().compose(reader), configFile);

            if (addDefaults) {
                config = addDefaults(stream);
            }

            config.save();
            return config;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return createNewConfig();
    }

    private YamlConfiguration createNewConfig() {
        return new YamlConfiguration(createNewMappingNode(), configFile);
    }

    public static MappingNode createNewMappingNode() {
        return new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
    }


    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    private boolean save() {
        if (config != null) { return config.save(); }
        return false;
    }

    private static final DecimalFormat DF_THOUSANDS = new DecimalFormat("#,###");
    private YamlConfiguration addDefaults(@Nullable Supplier<InputStream> defStreamSupplier) throws IOException {
        // Use passed arg unless it's null, then grab the IS from the plugin
        InputStream defConfigStream = getIS(defStreamSupplier);

        // Error if we still don't have a default config stream
        if (defConfigStream == null) {
            error("Error: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!");
            save();
            return config;
        }

        // InputStream and Reader both contain comments (verified)
        Reader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);

        MemoryConfiguration defConfig = new MemoryConfiguration((MappingNode) (KamiCommon.getYaml()).compose(reader));

        boolean needsNewKeys = false;
        for (String key : defConfig.getKeys(true)) {
            if (!config.contains(key)) { needsNewKeys = true; break; }
        }

        // This is a massive optimization, because if we need to insert new defaults, it requires a
        //  full recreate and rewrite of the config file, which is very slow
        if (!needsNewKeys) { return config; }

        Pair<List<String>, List<NodePair>> pair2 = YAMLParser.parseOrderedKeys(getIS(defStreamSupplier));
        List<String> keys = pair2.getA();
        List<NodePair> defaultKeyNodes = pair2.getB();

        // Add any existing keys that aren't in the defaults list
        // this will make any keys set by the plugin, that aren't in the defaults, stay
        for (String key : config.getKeys(true)) {
            if (!keys.contains(key)) { keys.add(key); }
        }

        YamlConfiguration newConfig = createNewConfig();

        // Compile the Nodes for the user config and default config
        //   We can do this while we update defaults, saving cycles later by using this cached data
        List<NodePair> configKeyNodes = new ArrayList<>();
        for (String key : keys) {
            // Fetch the config NodeTuple if it exists, and add it to the list (for copying comments)
            NodeTuple tuple = config.getNodeTuple(key);
            if (tuple != null) { configKeyNodes.add(new NodePair(key, (ScalarNode) tuple.getKeyNode(), true)); }

            // Fetch the value we want in the newConfig, and add it to the newConfig
            //   Ignore null since this is a new config, setting to null is a waste of time
            Object v = (tuple != null) ? MemorySection.getNodeValue(tuple.getValueNode()) : defConfig.get(key);

            if (v == null) { continue; }
            newConfig.internalPut(key, v);
        }

        // Copy comments the user might have placed in the file
        copyCommentsFromDefault(newConfig, defaultKeyNodes, abstractConfig.isDefaultCommentsOverwrite());
        // Copy comments from the default config (they will override for each specific instance)
        copyCommentsFromDefault(newConfig, configKeyNodes, abstractConfig.isDefaultCommentsOverwrite());

        return newConfig;
    }

    private InputStream getIS(@Nullable Supplier<InputStream> defStreamSupplier) {
        return (defStreamSupplier == null) ? getIS() : defStreamSupplier.get();
    }

    public abstract InputStream getIS();

    public abstract void error(String s);

    @SuppressWarnings("SameParameterValue")
    private String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    private boolean isInteger(String s) {
        try { Integer.parseInt(s); return true;
        } catch (NumberFormatException e) { return false; }
    }

    private boolean equalLists(List<String> l1, Set<String> l2) {
        // Check if l1 and l2 have the same items, regardless of order
        if (l1.size() != l2.size()) {
            return false;
        }
        for (String item : l1) {
            if (!l2.contains(item)) {
                return false;
            }
        }
        return true;
    }


    private static class YAMLParser {

        public static Pair<List<String>, List<NodePair>> parseOrderedKeys(InputStream stream) {
            List<String> valueKeys = new ArrayList<>();
            List<NodePair> allKeyNodes = new ArrayList<>();

            Node rootNode = KamiCommon.getYaml().compose(new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (rootNode instanceof MappingNode) {
                processYAMLNode(rootNode, valueKeys, allKeyNodes, "");
            }

            return Pair.of(valueKeys, allKeyNodes);
        }

        public static List<NodePair> parseOrderedNodes(InputStream stream) {
            List<NodePair> allKeyNodes = new ArrayList<>();

            Node rootNode = KamiCommon.getYaml().compose(new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (rootNode instanceof MappingNode) {
                processYAMLNode(rootNode, null, allKeyNodes, "");
            }

            return allKeyNodes;
        }

        private static void processYAMLNode(Node node, @Nullable List<String> keysWithValues, List<NodePair> allKeyNodes, String parentKey) {
            if (node instanceof MappingNode) {
                MappingNode mappingNode = (MappingNode) node;

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

    /**
     * @param config The config to copy comments to
     * @param keyNodes NodePairs of the keys to copy comments from
     */
    public static void copyCommentsFromDefault(YamlConfiguration config, List<NodePair> keyNodes, boolean defOverwrites) {
        for (NodePair nodePair : keyNodes) {
            copyCommentFromDefault(config, nodePair, defOverwrites);
        }
    }

    private static void copyCommentFromDefault(YamlConfiguration config, NodePair nodePair, boolean defOverwrites) {
        Node defNode = nodePair.scalarNode;
        if (defNode == null) { return; }

        // Optimization to skip if the default node doesn't have comments
        boolean blocks = (defNode.getBlockComments() != null && !defNode.getBlockComments().isEmpty());
        boolean inLine = (defNode.getInLineComments() != null && !defNode.getInLineComments().isEmpty());
        boolean end = (defNode.getEndComments() != null && !defNode.getEndComments().isEmpty());
        if (!blocks && !inLine && !end) { return; }

        // The keyNode in the NodeTuple from a MappingNode's values contains the comments, not the value node
        Node thisNode = config.getKeyNode(nodePair.key);
        if (thisNode == null) { return; }

        // Set the comments that are in the default config (but we can leave ones that people set)
        if (blocks && defOverwrites || thisNode.getBlockComments() == null || thisNode.getBlockComments().isEmpty()) {
            thisNode.setBlockComments(defNode.getBlockComments());
        }
        if (inLine && defOverwrites || thisNode.getInLineComments() == null || thisNode.getInLineComments().isEmpty()) {
            thisNode.setInLineComments(defNode.getInLineComments());
        }
        if (end && defOverwrites || thisNode.getEndComments() == null || thisNode.getEndComments().isEmpty()) {
            thisNode.setEndComments(defNode.getEndComments());
        }
    }
}

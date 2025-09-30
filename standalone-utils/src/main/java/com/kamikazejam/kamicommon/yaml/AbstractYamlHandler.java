package com.kamikazejam.kamicommon.yaml;

import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class AbstractYamlHandler<T extends AbstractYamlConfiguration> {
    protected final @NotNull AbstractConfig<?> abstractConfig;
    protected final @NotNull ConfigSource source;
    protected final @Nullable Supplier<InputStream> defaultsStream;
    // Should not be null after loadConfig() is called
    protected @Nullable T config;

    /**
     * @param abstractConfig The parent config instance who holds this handler.
     * @param source The source of the configuration file (yaml content).
     * @param defaultsStream An optional stream (of a YAML config) to read default values from, can be null.
     */
    public AbstractYamlHandler(
            @NotNull AbstractConfig<?> abstractConfig,
            @NotNull ConfigSource source,
            @Nullable Supplier<InputStream> defaultsStream
    ) {
        this.abstractConfig = abstractConfig;
        this.source = source;
        this.defaultsStream = defaultsStream;
        this.config = null;
    }

    @NotNull
    public static MappingNode createNewMappingNode() {
        return new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
    }

    public abstract @NotNull T newConfig(@NotNull MappingNode node, @NotNull ConfigSource source);

    public abstract @NotNull MemorySectionMethods<?> newMemorySection(@NotNull MappingNode node);

    @NotNull
    public T loadConfig() {
        try {
            // If writable file source, ensure file exists (and that dirs exist)
            if (source.isWritable()) {
                try {
                    source.ensureExistsIfWritable();
                } catch (IOException e) {
                    error("Could not prepare writable source (" + source.id() + "): " + e.getMessage());
                }
            }

            Optional<InputStream> opt = source.openStream();
            MappingNode rootNode;

            // Parse the content into a yaml mapping node
            if (opt.isPresent()) {
                try (Reader reader = new InputStreamReader(opt.get(), StandardCharsets.UTF_8)) {
                    // Require that the yaml root is parsed as a map
                    rootNode = (MappingNode) YamlUtil.getYaml().compose(reader);
                }
            } else {
                // No data available from source, create empty root node
                rootNode = createNewMappingNode();
            }

            // Create the new config object
            @NotNull T config = newConfig(rootNode, source);

            // Add defaults if a stream was provided
            if (defaultsStream != null) {
                config = addDefaults(defaultsStream);
            }

            // Persist only if the source is writable (i.e. file-based)
            if (source.isWritable()) {
                save();
            }

            return this.config = config;
        }catch (IOException e) {
            e.printStackTrace();
            // Fallback to empty config on error
            return newConfig(createNewMappingNode(), source);
        }
    }

    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    private boolean save() {
        // Require a valid config and writable source
        if (config == null) return false;
        if (!source.isWritable()) return false;
        return config.save();
    }

    private static final DecimalFormat DF_THOUSANDS = new DecimalFormat("#,###");
    private T addDefaults(@NotNull Supplier<InputStream> defaultsStream) {
        if (config == null) throw new IllegalStateException("Config must be loaded before adding defaults!");

        // Use passed arg unless it's null, then grab the IS from the plugin
        InputStream defConfigStream = defaultsStream.get();

        // Error if we still don't have a default config stream
        if (defConfigStream == null) {
            error("Error: Could NOT find config resource (" + source.id() + "), could not add defaults!");
            save();
            return config;
        }

        // InputStream and Reader both contain comments (verified)
        Reader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);

        MemorySectionMethods<?> defConfig = newMemorySection((MappingNode) (YamlUtil.getYaml()).compose(reader));

        // TODO: optimize add defaults to skip if no new keys and no new comments are required for a file
        // We used to skip all the logic below if the config did not need any new keys (based on what keys were in the default file)
        // HOWEVER, this broke the ability to update or add comments from the default file, since we skipped that logic below
        // I do not have a good solution for this currently. The penalty of writing the file back will have to be accepted for now
        // Perhaps we can analyze which part of the below logic is slow, and optimize that part
        //  maybe with a file contents comparison before writing the file (if file writing is slow)

        Pair<List<String>, List<NodePair>> pair2 = YAMLParser.parseOrderedKeys(defaultsStream.get());
        List<String> defaultKeys = pair2.getA();
        List<NodePair> defaultKeyNodes = pair2.getB();

        // Add any existing keys that aren't in the defaults list
        // this will make any keys set by the plugin, that aren't in the defaults, stay
        for (String key : config.getKeys(true)) {
            if (!defaultKeys.contains(key)) { defaultKeys.add(key); }
        }

        T newConfig = newConfig(createNewMappingNode(), source);

        // Compile the Nodes for the user config and default config
        //   We can do this while we update defaults, saving cycles later by using this cached data
        List<NodePair> configKeyNodes = new ArrayList<>();
        for (String key : defaultKeys) {
            // Fetch the config NodeTuple if it exists, and add it to the list (for copying comments)
            NodeTuple tuple = config.getNodeTuple(key);
            if (tuple != null) { configKeyNodes.add(new NodePair(key, (ScalarNode) tuple.getKeyNode(), true)); }

            // Fetch the value we want in the newConfig, and add it to the newConfig
            //   Ignore null since this is a new config, setting to null is a waste of time
            Object v = (tuple != null) ? AbstractMemorySection.getNodeValue(tuple.getValueNode()) : defConfig.get(key);

            if (v == null) { continue; }
            newConfig.internalPut(key, v);
        }

        // Copy comments the user might have placed in the file
        copyCommentsFromDefault(newConfig, defaultKeyNodes, abstractConfig.isDefaultCommentsOverwrite());
        // Copy comments from the default config (they will override for each specific instance)
        copyCommentsFromDefault(newConfig, configKeyNodes, abstractConfig.isDefaultCommentsOverwrite());

        return newConfig;
    }

    public abstract void error(String s);

    public abstract void warn(String s);

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

    /**
     * @param config The config to copy comments to
     * @param keyNodes NodePairs of the keys to copy comments from
     */
    public static void copyCommentsFromDefault(AbstractYamlConfiguration config, List<NodePair> keyNodes, boolean defOverwrites) {
        for (NodePair nodePair : keyNodes) {
            copyCommentFromDefault(config, nodePair, defOverwrites);
        }
    }

    private static void copyCommentFromDefault(AbstractYamlConfiguration config, NodePair nodePair, boolean defOverwrites) {
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

    public record NodePair(String key, ScalarNode scalarNode, boolean terminatesInValue) { }

    private @Nullable Set<String> getBlockComments(@Nullable NodeTuple tuple) {
        if (tuple == null) { return null; }
        if (!(tuple.getKeyNode() instanceof ScalarNode scalarNode)) { return null; }

        @Nullable List<CommentLine> comments = scalarNode.getBlockComments();
        if (comments == null || comments.isEmpty()) { return null; }

        return comments.stream().map(CommentLine::getValue).collect(Collectors.toSet());
    }
    private boolean isExempt(@Nullable Set<String> comments1, @Nullable Set<String> comments2) {
        if (comments1 != null) {
            for (String comment : comments1) {
                if (comment.contains("@keep")) { return true; }
            }
        }
        if (comments2 != null) {
            for (String comment : comments2) {
                if (comment.contains("@keep")) { return true; }
            }
        }
        return false;
    }
}

package com.kamikazejam.kamicommon.yaml.util;

import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.AbstractMemorySection;
import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;
import com.kamikazejam.kamicommon.yaml.AbstractYamlHandler;
import com.kamikazejam.kamicommon.yaml.base.MemorySectionMethods;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class YamlDefaultsUtil {
    @Internal
    public static <T extends AbstractYamlConfiguration> T addDefaults(
            @NotNull AbstractYamlHandler<T> handler,
            @NotNull Supplier<InputStream> defaultsStream,
            @NotNull T config
    ) {
        Preconditions.checkNotNull(defaultsStream, "Defaults stream cannot be null when adding defaults!");
        Preconditions.checkNotNull(config, "Config must be loaded before adding defaults!");

        // Use passed arg unless it's null, then grab the IS from the plugin
        InputStream defConfigStream = defaultsStream.get();

        // Error if we still don't have a default config stream
        if (defConfigStream == null) {
            handler.error("Error: Could NOT find config resource (" + handler.source.id() + "), could not add defaults!");
            handler.save();
            return config;
        }

        // InputStream and Reader both contain comments (verified)
        Reader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);

        MemorySectionMethods<?> defConfig = handler.newMemorySection((MappingNode) (YamlUtil.getYaml()).compose(reader));

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

        T newConfig = handler.newConfig(AbstractYamlHandler.createNewMappingNode(), handler.source);

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
        copyCommentsFromDefault(newConfig, defaultKeyNodes, handler.abstractConfig.isDefaultCommentsOverwrite());
        // Copy comments from the default config (they will override for each specific instance)
        copyCommentsFromDefault(newConfig, configKeyNodes, handler.abstractConfig.isDefaultCommentsOverwrite());

        return newConfig;
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
        Node defNode = nodePair.scalarNode();
        if (defNode == null) { return; }

        // Optimization to skip if the default node doesn't have comments
        boolean blocks = (defNode.getBlockComments() != null && !defNode.getBlockComments().isEmpty());
        boolean inLine = (defNode.getInLineComments() != null && !defNode.getInLineComments().isEmpty());
        boolean end = (defNode.getEndComments() != null && !defNode.getEndComments().isEmpty());
        if (!blocks && !inLine && !end) { return; }

        // The keyNode in the NodeTuple from a MappingNode's values contains the comments, not the value node
        Node thisNode = config.getKeyNode(nodePair.key());
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

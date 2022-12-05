package com.kamikazejamplugins.kamicommon.config;

import com.kamikazejamplugins.kamicommon.config.annotation.ConfigValue;
import com.kamikazejamplugins.kamicommon.config.data.ConfigComment;
import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.YamlHandler;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class KamiConfigManager {

    /**
     * Loads a config .yml file into a KamiConfig
     * @param kamiConfig The KamiConfig to load its file into
     */
    public static YamlHandler.YamlConfiguration loadKamiConfigFromFile(KamiConfig kamiConfig) {
        File file = kamiConfig.getFile();
        YamlHandler yamlHandler = new YamlHandler(file);
        YamlHandler.YamlConfiguration config = yamlHandler.loadConfig(false);

        // Key, FieldName
        Map<String, String> fieldMappings = new HashMap<>();

        // Fill the Field mappings
        for (Field field : kamiConfig.getClass().getDeclaredFields()) {
            ConfigValue annotation;

            field.setAccessible(true);
            if (!field.isAnnotationPresent(ConfigValue.class)) { continue; }
            annotation = field.getAnnotation(ConfigValue.class);

            // Define the fieldName (split by _ and take only the last argument)
            String fieldName = field.getName().split("_")[field.getName().split("_").length-1];

            // Build the key from the path and the field Name
            String key = (annotation.path().isEmpty()) ? fieldName : annotation.path() + "." + fieldName;

            // Store it
            fieldMappings.put(key, field.getName());
        }

        // Loop through all the keys in the config, and update the KamiConfig variables
        for (String key : config.getKeys(true)) {
            try {
                String fieldName = fieldMappings.getOrDefault(key, null);
                if (fieldName == null) { continue; }
                Field field = kamiConfig.getClass().getDeclaredField(fieldName);

                field.setAccessible(true);
                field.set(kamiConfig, config.get(key));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    /**
     * Saves KamiConfig(s) to their file(s)
     * @param kamiConfig The KamiConfig to save
     */
    public static void saveKamiConfigToFile(KamiConfig kamiConfig) {
        try {
            saveInternal(kamiConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveInternal(KamiConfig kamiConfig) throws Exception {
        YamlHandler yamlHandler = new YamlHandler(kamiConfig.getFile());
        YamlHandler.YamlConfiguration config = yamlHandler.loadConfig(false);

        List<ConfigComment> comments = new ArrayList<>();

        // Loop through all the fields in the KamiConfig
        for (Field field : kamiConfig.getClass().getDeclaredFields()) {
            ConfigValue annotation;

            field.setAccessible(true);
            if (!field.isAnnotationPresent(ConfigValue.class)) { continue; }
            annotation = field.getAnnotation(ConfigValue.class);

            // Define the fieldName (split by _ and take only the last argument)
            String fieldName = field.getName().split("_")[field.getName().split("_").length-1];

            // Build the key from the path and the field Name
            String key = (annotation.path().isEmpty()) ? fieldName : annotation.path() + "." + fieldName;

            // Set the config values to the current KamiConfig value
            config.set(key, field.get(kamiConfig));

            // Add to the comments list for post processing
            if (annotation.above().length != 0) {
                int i = 0;
                String[] parts = key.split("\\.");

                for (String c : annotation.above()) {
                    if (!c.isEmpty()) {
                        String subKey = StringUtil.combine(StringUtil.subList(parts, 0, parts.length-i), ".");
                        comments.add(new ConfigComment(subKey, c, true));
                    }
                    i++;
                }
            }
            if (!annotation.inline().isEmpty()) {
                String c = annotation.inline();
                c = c.replace("\n", " ");
                comments.add(new ConfigComment(key, c, false));
            }
        }
        // Save the FileConfiguration (without comments)
        config.save();

        // Store the lines here so that we don't have to read the file multiple times
        List<String> lines = Files.readAllLines(kamiConfig.getFile().toPath(), StandardCharsets.UTF_8);

        // Add nice spacing
        saveWithSpaces(lines, kamiConfig);

        // Add the comments to the file
        for (ConfigComment comment : comments) {
            addComment(lines, comment);
        }

        Files.write(kamiConfig.getFile().toPath(), lines, StandardCharsets.UTF_8);
    }

    private static void saveWithSpaces(List<String> lines, KamiConfig kamiConfig) {
        // For every line that doesn't start with a space, or a comment, add a newline before it
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String starts = line.split(" ")[0];
            Pattern pattern = Pattern.compile("([\\w']+):");

            Matcher matcher = pattern.matcher(starts);
            if (i > 0 && matcher.find()) {
                String key = starts.replace(":", "");

                // Add a newLine above the key if there is going to also be a comment
                try {
                    Field field = kamiConfig.getClass().getDeclaredField(key);
                    field.setAccessible(true);
                    ConfigValue annotation = field.getAnnotation(ConfigValue.class);
                    if (annotation.above().length != 0) {
                        lines.add(i, "");
                        i++;
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private static void addComment(List<String> lines, ConfigComment comment) {
        String[] parts = comment.getKey().split("\\.");
        int searchingFor = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String start = StringUtil.repeat("  ", searchingFor) + parts[searchingFor] + ":";

            if (line.startsWith(start)) {
                if (searchingFor == parts.length - 1) {

                    // We've found the key we're looking for
                    if (comment.isAbove()) {
                        String spacing = StringUtil.repeat("  ", searchingFor);
                        String c = comment.getComment().replace("\n", "\n" + spacing + "# ");
                        lines.add(i, spacing + "# " + c);
                    }else {
                        lines.set(i, line + " # " + comment.getComment());
                    }
                    break;
                } else {
                    searchingFor++;
                }
            }
        }
    }
}

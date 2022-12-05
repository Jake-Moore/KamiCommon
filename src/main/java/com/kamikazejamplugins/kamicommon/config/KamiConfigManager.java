package com.kamikazejamplugins.kamicommon.config;

import com.kamikazejamplugins.kamicommon.config.annotation.ConfigValue;
import com.kamikazejamplugins.kamicommon.config.data.ConfigComment;
import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.YamlHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Add the comments to the file
        for (ConfigComment comment : comments) {
            addComment(kamiConfig.getFile(), comment);
        }
    }

    private static void addComment(File file, ConfigComment comment) throws IOException {
        String[] parts = comment.getKey().split("\\.");
        int searchingFor = 0;

        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

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

        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
    }
}

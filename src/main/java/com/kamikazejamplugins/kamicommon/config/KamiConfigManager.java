package com.kamikazejamplugins.kamicommon.config;

import com.kamikazejamplugins.kamicommon.config.data.ConfigComment;
import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.YamlHandler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@SuppressWarnings("unused")
public class KamiConfigManager {

    public static void saveKamiConfig(KamiConfig kamiConfig) throws Exception {
        YamlHandler.YamlConfiguration config = kamiConfig.getYamlConfiguration();
        List<ConfigComment> comments = kamiConfig.getComments();

        // Save the FileConfiguration (without comments)
        config.save();

        // Store the lines here so that we don't have to read the file multiple times
        List<String> lines = Files.readAllLines(kamiConfig.getFile().toPath(), StandardCharsets.UTF_8);

        // Add the comments to the file
        for (ConfigComment comment : comments) {
            addComment(lines, comment);
        }

        // Save the modified lines to the file
        Files.write(kamiConfig.getFile().toPath(), lines, StandardCharsets.UTF_8);
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
                        String[] commentLines = comment.getComment().toArray(new String[0]);
                        // We need to loop backwards since each line is added to the top of the key
                        for (int j = commentLines.length - 1; j >= 0; j--) {
                            String commentLine = commentLines[j];
                            if (commentLine.trim().isEmpty()) {
                                lines.add(i, "");
                            } else {
                                lines.add(i, spacing + "# " + commentLine.replaceAll("<br>", " "));
                            }
                        }
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

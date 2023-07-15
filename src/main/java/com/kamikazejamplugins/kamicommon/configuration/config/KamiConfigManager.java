package com.kamikazejamplugins.kamicommon.configuration.config;

import com.kamikazejamplugins.kamicommon.configuration.config.data.ConfigComment;
import com.kamikazejamplugins.kamicommon.util.StringUtil;
import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by AbstractConfig when saving. It should not be used
 */
@SuppressWarnings("unused")
class KamiConfigManager {

    protected static void saveKamiConfig(AbstractConfig abstractConfig) throws IOException {
        YamlConfiguration config = abstractConfig.getYamlConfiguration();
        List<ConfigComment> comments = abstractConfig.getComments();

        // Save the FileConfiguration (without additionally registered comments)
        config.save();

        // Store the lines here so that we don't have to read the file multiple times
        InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(abstractConfig.getFile().toPath()), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        List<String> lines = new ArrayList<>();
        while (reader.ready()) { lines.add(reader.readLine()); }

        // Add the comments to the file
        for (ConfigComment comment : comments) {
            addComment(lines, comment);
        }

        // Empty file verses an empty json {} weird stuff
        if (abstractConfig.isEmpty()) { lines.clear(); }

        // Save the modified lines to the file
        Files.write(abstractConfig.getFile().toPath(), convert(lines), StandardCharsets.ISO_8859_1);
    }

    // Convert our UTF-8 lines to ISO-8859-1, because we can write ISO-8859-1 to a file with special chars
    protected static List<String> convert(List<String> lines) {
        List<String> newLines = new ArrayList<>();
        for (String line : lines) {
            newLines.add(new String(line.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        }
        return newLines;
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

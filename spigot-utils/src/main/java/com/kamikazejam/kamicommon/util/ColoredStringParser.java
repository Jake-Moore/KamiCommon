package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Utility for mapping {@link String} objects into {@link VersionedComponent} objects,
 * handling both legacy color codes and modern MiniMessage format.<br>
 * <br>
 * See individual methods for specific parsing behavior.
 */
public class ColoredStringParser {
    /**
     * Identifies the type of title string being used, and tries its best to set it correctly on the builder.<br>
     * Supports (parsed in this order):<br>
     * - Legacy Section (contains &sect; symbols)<br>
     * - MiniMessage (contains &lt;tag&gt; tags)<br>
     * - Legacy Ampersand (contains &amp; symbols)<br>
     */
    public static @NotNull VersionedComponent parse(@NotNull String input) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        // 1. MiniMessage cannot support &sect; symbols, so if we find one, it's definitely legacy
        if (input.contains("§")) {
            // It contained at least one section symbol, so map those
            // NOTE: This also automatically translates legacy color codes.
            return serializer.fromLegacySection(LegacyColors.t(input));
        }

        // 2. If it contains <tag> symbols, it's most likely MiniMessage
        Pattern pattern = Pattern.compile("<[^<>]+>");
        if (input.contains("<\\") || pattern.matcher(input).find()) {
            // NOTE: MiniMessage will ignore ampersand color codes
            return serializer.fromMiniMessage(input);
        }

        // 3. If it contains & symbols, it's most likely legacy ampersand
        if (input.contains("&")) {
            // We automatically translate legacy color codes into section symbols first
            return serializer.fromLegacySection(LegacyColors.t(input));
        }

        // 4. Otherwise, just treat it as plain text
        // (can use the mini message parses since it won't error on plain text, it just won't do anything special)
        return serializer.fromMiniMessage(input);
    }
}

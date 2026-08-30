package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility for mapping {@link String} objects into {@link VersionedComponent} objects,
 * handling both legacy color codes and modern MiniMessage format.<br>
 * <br>
 * The format of a line is decided by {@link #detectFormat(String)}, which tests, in order:
 * legacy section, legacy ampersand, MiniMessage, then plain text. Legacy ampersand is tested
 * <i>before</i> MiniMessage on purpose: a v4 line such as {@code &7Usage: &f/kit <name>} carries
 * both a colour code and something that looks like a tag, and it is the colour code that says what
 * the author meant.<br>
 * <br>
 * See individual methods for specific parsing behavior.
 */
public class ColoredStringParser {

    /**
     * A legacy colour or formatting code: an ampersand followed by a code character.
     * <p>
     * Testing for {@code &} alone is not enough. Ordinary prose contains ampersands
     * ({@code <gray>Tips & Tricks}), and treating those as legacy input renders the MiniMessage
     * tags around them literally.
     */
    private static final Pattern LEGACY_AMPERSAND = Pattern.compile("&[0-9a-fk-orxA-FK-ORX]");

    /** A MiniMessage tag, e.g. {@code <gray>} or {@code </bold>}. */
    private static final Pattern MINI_MESSAGE_TAG = Pattern.compile("<[^<>]+>");

    /**
     * The four text formats {@link #parse(String)} can dispatch to, in the order they are tested.
     */
    enum ColorFormat {
        /** Contains at least one section symbol, so it is legacy however it got that way. */
        LEGACY_SECTION,
        /** Contains an ampersand followed by a legacy colour or formatting code. */
        LEGACY_AMPERSAND,
        /** Contains a MiniMessage tag and no legacy colour code. */
        MINI_MESSAGE,
        /** No colour markup of any kind. */
        PLAIN
    }

    /**
     * Decides which format {@code input} is written in.
     * <p>
     * Separate from {@link #parse(String)} so it can be unit-tested. {@code parse} returns a
     * {@link VersionedComponent} and therefore needs a live {@code NmsAPI}, which a unit test has
     * no way to provide, so the branch decision is the only part that can be checked off-server.
     */
    static @NotNull ColorFormat detectFormat(@NotNull String input) {
        // 1. MiniMessage cannot carry § symbols, so if we find one, it's definitely legacy.
        if (input.contains("§")) {
            return ColorFormat.LEGACY_SECTION;
        }

        // 2. A real legacy colour code beats a tag-shaped substring. Configured v4 text routinely
        //    contains both (&7Usage: &f/kit <name>), and rendering that as MiniMessage prints the
        //    colour codes literally. The code is the deliberate marker; the angle brackets are not.
        if (LEGACY_AMPERSAND.matcher(input).find()) {
            return ColorFormat.LEGACY_AMPERSAND;
        }

        // 3. No legacy code, so a <tag> means MiniMessage.
        if (input.contains("<\\") || MINI_MESSAGE_TAG.matcher(input).find()) {
            return ColorFormat.MINI_MESSAGE;
        }

        // 4. Otherwise, just treat it as plain text.
        return ColorFormat.PLAIN;
    }

    /**
     * Identifies the type of string being used, and tries its best to map it into a component.<br>
     * Supports (parsed in this order):<br>
     * - Legacy Section (contains &sect; symbols)<br>
     * - Legacy Ampersand (contains an &amp; followed by a colour or formatting code)<br>
     * - MiniMessage (contains &lt;tag&gt; tags)<br>
     * - Plain text (no colour markup at all)<br>
     */
    public static @NotNull VersionedComponent parse(@NotNull String input) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        switch (detectFormat(input)) {
            case LEGACY_SECTION:
            case LEGACY_AMPERSAND:
                // One branch for both: LegacyColors.t translates ampersand codes into section
                // symbols, so section-coded input passes through it unchanged.
                return serializer.fromLegacySection(LegacyColors.t(input));
            case MINI_MESSAGE:
                // NOTE: MiniMessage will ignore ampersand color codes
                return serializer.fromMiniMessage(input);
            case PLAIN:
                // Can use the MiniMessage parser since it won't error on plain text,
                // it just won't do anything special.
                return serializer.fromMiniMessage(input);
            default:
                throw new IllegalStateException("unhandled ColorFormat: " + detectFormat(input));
        }
    }

    /**
     * Parses a list of strings into a list of {@link VersionedComponent} objects, using the same logic as {@link #parse(String)} for each line.<br>
     * <br>
     * This is useful for parsing lore lists from configuration files or other sources.
     */
    public static @NotNull List<VersionedComponent> parse(@NotNull List<String> input) {
        return input.stream().map(ColoredStringParser::parse).collect(Collectors.toList());
    }
}

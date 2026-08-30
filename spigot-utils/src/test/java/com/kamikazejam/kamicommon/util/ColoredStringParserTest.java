package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.util.ColoredStringParser.ColorFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Covers {@link ColoredStringParser#detectFormat(String)}, which is the whole of the branch
 * decision {@code parse} makes.
 * <p>
 * {@code parse} itself cannot be tested here: it returns a {@code VersionedComponent} and needs a
 * live {@code NmsAPI}, which only exists on a running server. Splitting the decision out is what
 * makes any of this checkable off-server.
 */
class ColoredStringParserTest {

    private static void assertFormat(ColorFormat expected, String input) {
        assertEquals(expected, ColoredStringParser.detectFormat(input),
                "detectFormat(\"" + input + "\")");
    }

    @Test
    @DisplayName("a legacy colour code wins over a tag-shaped substring")
    void legacyAmpersandBeatsAngleBrackets() {
        // The regression this file exists for. Every one of these is ordinary v4 configured text
        // that ALSO happens to contain angle brackets, and every one of them used to be handed to
        // MiniMessage, which prints "&7" and friends literally.
        assertAll(
                () -> assertFormat(ColorFormat.LEGACY_AMPERSAND, "&7Usage: &f/kit <name>"),
                () -> assertFormat(ColorFormat.LEGACY_AMPERSAND, "&7Backpack <Page 1>"),
                () -> assertFormat(ColorFormat.LEGACY_AMPERSAND, "&cClick to <buy>"),
                () -> assertFormat(ColorFormat.LEGACY_AMPERSAND, "&aShop <3"),
                () -> assertFormat(ColorFormat.LEGACY_AMPERSAND, "&eBalance: &a$1,000")
        );
    }

    @Test
    @DisplayName("MiniMessage wins when there is no legacy colour code")
    void miniMessageWithoutLegacyCodes() {
        assertAll(
                // A bare ampersand here is PROSE, not a colour code: "& T" is not a legacy code,
                // because "T" is not one of 0-9a-fk-orx. This is precisely why the discriminator
                // tests for an ampersand FOLLOWED BY a code character rather than for the presence
                // of an ampersand alone - the old `input.contains("&")` test would have called this
                // legacy and rendered the <gray> tag literally.
                () -> assertFormat(ColorFormat.MINI_MESSAGE, "<gray>Tips & Tricks"),
                () -> assertFormat(ColorFormat.MINI_MESSAGE, "<gray>Hello"),
                () -> assertFormat(ColorFormat.MINI_MESSAGE, "<red>SALE <bold>50% off")
        );
    }

    @Test
    @DisplayName("a section symbol is proof of legacy input, whatever else is present")
    void sectionSymbolIsCheckedFirst() {
        assertFormat(ColorFormat.LEGACY_SECTION, "§7already sectioned");
    }

    @Test
    @DisplayName("text with no markup at all is plain")
    void plainText() {
        assertFormat(ColorFormat.PLAIN, "Plain text");
    }
}

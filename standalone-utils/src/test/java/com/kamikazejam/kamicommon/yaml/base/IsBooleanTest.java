package com.kamikazejam.kamicommon.yaml.base;

import com.kamikazejam.kamicommon.yaml.standalone.MemorySectionStandalone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers {@link MemorySectionMethods#isBoolean(String)}.
 * <p>
 * It was {@code get(key) instanceof Boolean}, and {@code AbstractMemorySection} returns every
 * scalar node as a {@link String}, so it could never be true for a value that came out of a yaml
 * file. Its sibling {@code isInt} does not have the problem because it routes through
 * {@code getBigDecimal}, which parses the string; {@code isBoolean} was the odd one out.
 * <p>
 * That made three documented config keys inert, because each was gated behind it: {@code
 * unbreakable:} and {@code hide-attributes:} in {@code ItemBuilderLoader} never applied, and
 * {@code hide-attributes: false} on a menu icon could not switch the global default off.
 * <p>
 * The contract these pin: <b>{@code isBoolean} is true exactly when {@code getBoolean} would parse
 * the value rather than fall back to its default.</b>
 */
class IsBooleanTest {

    private static final String YAML =
            "realTrue: true\n" +
            "realFalse: false\n" +
            "quotedTrue: 'true'\n" +
            "quotedFalse: 'false'\n" +
            "upperTrue: 'TRUE'\n" +
            "mixedFalse: 'False'\n" +
            "yes1: 'yes'\n" +
            "no1: 'no'\n" +
            "on1: 'on'\n" +
            "off1: 'off'\n" +
            "word: 'banana'\n" +
            "empty: ''\n" +
            "number: 7\n" +
            "numericOne: 1\n" +
            "section:\n" +
            "  nested: true\n";

    private static MemorySectionStandalone section() {
        MappingNode node = (MappingNode) new Yaml().compose(new StringReader(YAML));
        return new MemorySectionStandalone(node, "", null);
    }

    @Test
    @DisplayName("a real yaml boolean is a boolean")
    void realBooleans() {
        MemorySectionStandalone s = section();
        assertAll(
                () -> assertTrue(s.isBoolean("realTrue")),
                () -> assertTrue(s.isBoolean("realFalse"))
        );
    }

    @Test
    @DisplayName("the quoted spellings getBoolean accepts are booleans too")
    void quotedSpellings() {
        MemorySectionStandalone s = section();
        assertAll(
                () -> assertTrue(s.isBoolean("quotedTrue"), "'true'"),
                () -> assertTrue(s.isBoolean("quotedFalse"), "'false'"),
                () -> assertTrue(s.isBoolean("upperTrue"), "'TRUE'"),
                () -> assertTrue(s.isBoolean("mixedFalse"), "'False'"),
                () -> assertTrue(s.isBoolean("yes1"), "'yes'"),
                () -> assertTrue(s.isBoolean("no1"), "'no'"),
                () -> assertTrue(s.isBoolean("on1"), "'on'"),
                () -> assertTrue(s.isBoolean("off1"), "'off'")
        );
    }

    @Test
    @DisplayName("things getBoolean would not parse are not booleans")
    void nonBooleans() {
        MemorySectionStandalone s = section();
        assertAll(
                () -> assertFalse(s.isBoolean("word"), "a plain word"),
                () -> assertFalse(s.isBoolean("empty"), "an empty string"),
                () -> assertFalse(s.isBoolean("number"), "a number"),
                // 1/0 are NOT accepted by getBoolean, so isBoolean must not accept them either.
                () -> assertFalse(s.isBoolean("numericOne"), "the number 1"),
                () -> assertFalse(s.isBoolean("section"), "a nested section")
        );
    }

    @Test
    @DisplayName("a missing key is not a boolean")
    void missingKey() {
        MemorySectionStandalone s = section();
        assertAll(
                () -> assertFalse(s.isBoolean("nope")),
                () -> assertFalse(s.isBoolean("section.nope")),
                () -> assertTrue(s.isBoolean("section.nested"), "a nested real boolean IS one")
        );
    }

    @Test
    @DisplayName("isBoolean agrees with getBoolean on every key")
    void agreesWithGetBoolean() {
        MemorySectionStandalone s = section();
        // The invariant, stated directly: when isBoolean is true, getBoolean must return the value
        // rather than either default. When it is false, getBoolean must return whatever default it
        // was handed.
        for (String key : new String[]{"realTrue", "realFalse", "quotedTrue", "quotedFalse",
                "upperTrue", "mixedFalse", "yes1", "no1", "on1", "off1",
                "word", "empty", "number", "numericOne", "section", "nope"}) {
            boolean claimed = s.isBoolean(key);
            boolean withTrueDefault = s.getBoolean(key, true);
            boolean withFalseDefault = s.getBoolean(key, false);
            if (claimed) {
                assertEquals(withTrueDefault, withFalseDefault,
                        "isBoolean said '" + key + "' is a boolean, but getBoolean fell back to its "
                                + "default, so the two disagree");
            } else {
                assertTrue(withTrueDefault && !withFalseDefault,
                        "isBoolean said '" + key + "' is NOT a boolean, but getBoolean parsed it "
                                + "anyway, so the two disagree");
            }
        }
    }
}

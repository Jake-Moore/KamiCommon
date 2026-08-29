package com.kamikazejam.kamicommon.util.nms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NmsVersionParserTest {

    /**
     * The implementation as it stood before Paper's calendar versions, copied verbatim. Its only
     * job here is to prove the replacement did not move a single legacy threshold.
     */
    private static int legacyImpl(String mcVer) {
        long num = mcVer.chars().filter(ch -> ch == '.').count();
        String s;
        if (num == 1) {
            String t = mcVer.replaceAll("\\.", "");
            if (t.length() == 2) {
                s = mcVer.replaceAll("\\.", "0") + "0";
            } else {
                s = t + "0";
            }
        } else if (num == 2) {
            String[] parts = mcVer.split("\\.");
            if (parts[1].length() == 1) {
                s = parts[0] + "0" + parts[1] + parts[2];
            } else {
                s = parts[0] + parts[1] + parts[2];
            }
        } else {
            throw new IllegalArgumentException("Unknown version format: " + mcVer);
        }
        return Integer.parseInt(s);
    }

    /**
     * Every threshold that appears in a {@code f("...")} call anywhere in KamiCommonNMS, plus the
     * versions this estate has actually run. 1.21.10 and 1.21.11 are the two that catch a naive
     * arithmetic rewrite, because the original packing is textual.
     */
    private static final String[] LEGACY_VERSIONS = {
            "1.8", "1.8.3", "1.8.8", "1.9", "1.9.2", "1.9.4", "1.10.2", "1.11", "1.11.2",
            "1.12", "1.12.2", "1.13", "1.13.2", "1.14.4", "1.15.2", "1.16", "1.16.1", "1.16.3",
            "1.16.5", "1.17", "1.17.1", "1.18.1", "1.18.2", "1.19.2", "1.19.3", "1.19.4",
            "1.20.1", "1.20.2", "1.20.4", "1.20.6", "1.21.4", "1.21.9", "1.21.10", "1.21.11",
    };

    @Test
    @DisplayName("every legacy version keeps exactly the value the original produced")
    void legacyValuesAreUnchanged() {
        // Guard the guard: a loop over an empty array passes forever.
        assertTrue(LEGACY_VERSIONS.length >= 30, "expected the full threshold list");
        for (String v : LEGACY_VERSIONS) {
            assertEquals(legacyImpl(v), NmsVersionParser.getFormattedNmsInteger(v),
                    "value moved for " + v);
        }
    }

    @Test
    @DisplayName("the textual packing is preserved for two-digit patches")
    void twoDigitPatchesStillWiden() {
        assertEquals(1219, NmsVersionParser.getFormattedNmsInteger("1.21.9"));
        assertEquals(12110, NmsVersionParser.getFormattedNmsInteger("1.21.10"));
        assertEquals(12111, NmsVersionParser.getFormattedNmsInteger("1.21.11"));
    }

    @Test
    @DisplayName("Paper 26.x's own version string parses instead of throwing")
    void paperCalendarCoordinatesParse() {
        // What Bukkit.getBukkitVersion() reports on 26.x, after the caller's split("-")[0].
        assertEquals(260200, NmsVersionParser.getFormattedNmsInteger("26.2.build.115"));
        assertEquals(260200, NmsVersionParser.getFormattedNmsInteger("26.2.build.120"));
        assertEquals(260102, NmsVersionParser.getFormattedNmsInteger("26.1.2.build.7"));
        // And the clean form, which Server#getMinecraftVersion() reports.
        assertEquals(260200, NmsVersionParser.getFormattedNmsInteger("26.2"));
        assertEquals(260101, NmsVersionParser.getFormattedNmsInteger("26.1.1"));
    }

    @Test
    @DisplayName("versions sort in release order across the legacy/calendar boundary")
    void orderingIsMonotonic() {
        String[] inReleaseOrder = {
                "1.8", "1.8.8", "1.16.5", "1.20.4", "1.21.4", "1.21.9", "1.21.10", "1.21.11",
                "26.1.1", "26.1.2", "26.2", "26.10", "27.1",
        };
        for (int i = 1; i < inReleaseOrder.length; i++) {
            int prev = NmsVersionParser.getFormattedNmsInteger(inReleaseOrder[i - 1]);
            int curr = NmsVersionParser.getFormattedNmsInteger(inReleaseOrder[i]);
            assertTrue(curr > prev, inReleaseOrder[i] + " (" + curr + ") must sort above "
                    + inReleaseOrder[i - 1] + " (" + prev + ")");
        }
    }

    @Test
    @DisplayName("a 26.x server outranks every threshold the providers branch on")
    void calendarVersionsClearEveryLegacyThreshold() {
        int paper262 = NmsVersionParser.getFormattedNmsInteger("26.2");
        for (String v : LEGACY_VERSIONS) {
            assertTrue(paper262 > NmsVersionParser.getFormattedNmsInteger(v),
                    "26.2 must outrank " + v);
        }
    }
}

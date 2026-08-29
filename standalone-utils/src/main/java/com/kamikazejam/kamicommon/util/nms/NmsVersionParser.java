package com.kamikazejam.kamicommon.util.nms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class NmsVersionParser {

    /**
     * Matches the leading numeric, dot-separated components of a version string and stops at the
     * first component that is not a number.
     * <p>
     * This tolerance is required rather than cosmetic. Paper's 26.x releases report
     * {@code Bukkit.getBukkitVersion()} as e.g. {@code "26.2.build.115-stable"} (read from
     * {@code apiVersioning.json}), so after the usual {@code split("-")[0]} the caller hands us
     * {@code "26.2.build.115"}. Anything that insists on exactly two or three dot-separated
     * numbers throws on every 26.x server.
     */
    private static final Pattern LEADING_NUMERIC =
            Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?");

    /**
     * Converts a Minecraft version string into an integer that sorts in release order.
     * <p>
     * Two eras, because Minecraft left {@code 1.x} versioning behind after 1.21.11 and moved to
     * calendar versions ({@code 26.1.1}, {@code 26.1.2}, {@code 26.2}, ...):
     * <ul>
     *   <li><b>Legacy ({@code 1.x}):</b> the original packing is reproduced exactly, digit for
     *       digit, so every existing {@code f("1.x.y")} threshold keeps the value it has always
     *       had. Note the packing is textual rather than arithmetic, which is why
     *       {@code 1.21.10} is {@code 12110} and not {@code 1220}.</li>
     *   <li><b>Calendar ({@code >= 2.x}):</b> {@code major*10_000 + minor*100 + patch}, two digits
     *       each, so {@code 26.2} reads as {@code 26|02|00} and gives
     *       {@code f("26.2") == 260200}, {@code f("26.1.2") == 260102}. Every value lands far
     *       above every legacy value, since the legacy branch tops out at
     *       {@code "1" + "99" + "99" == 19999}, so no offset is needed and the two eras cannot
     *       overlap. It also stays monotonic, which the old 4-digit packing did not: that gave
     *       {@code f("26.2") == 2620}, sorting <i>below</i> {@code f("1.21.11") == 12111}.</li>
     * </ul>
     *
     * @param mcVer The MC version string (i.e. "1.20.4", "1.8.8", "26.2", or "26.2.build.115")
     * @return The version as an order-preserving integer
     */
    public static int getFormattedNmsInteger(String mcVer) {
        Matcher matcher = LEADING_NUMERIC.matcher(mcVer.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Unknown version format: " + mcVer);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = (matcher.group(2) == null) ? 0 : Integer.parseInt(matcher.group(2));
        int patch = (matcher.group(3) == null) ? 0 : Integer.parseInt(matcher.group(3));

        if (major == 1) {
            // Legacy era. Kept textual so that a two-digit patch widens the result the way it
            //  always has: "1" + "21" + "10" -> 12110.
            String packed = (minor <= 9)
                    ? ("1" + "0" + minor + patch)
                    : ("1" + minor + patch);
            return Integer.parseInt(packed);
        }

        // CALENDAR ERA (26.1.1, 26.1.2, 26.2, ...). Two digits each, so 26.2 reads as 26|02|00.
        //  The legacy branch above tops out at "1"+"99"+"99" = 19999, and any value here is at
        //  least 260000, so the eras cannot overlap. Assumes minor and patch stay under 100,
        //  which is the same assumption the legacy packing already makes.
        return (major * 10_000) + (minor * 100) + patch;
    }
}

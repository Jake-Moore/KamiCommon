package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import com.kamikazejam.kamicommon.util.predicate.PredicateStartsWithIgnoreCase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.kamikazejam.kamicommon.util.Txt.Config.TITLE_LINE_LENGTH;

@SuppressWarnings({"UnnecessaryUnicodeEscape", "unused"})
public class Txt {

    public static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    public static final Pattern PATTERN_NEWLINE = Pattern.compile("\\r?\\n");
    public static final long millisPerSecond = 1000;
    public static final long millisPerMinute = 60 * millisPerSecond;
    public static final long millisPerHour = 60 * millisPerMinute;
    public static final long millisPerDay = 24 * millisPerHour;
    public static final long millisPerWeek = 7 * millisPerDay;
    public static final long millisPerMonth = 31 * millisPerDay;
    public static final long millisPerYear = 365 * millisPerDay;
    public static final Set<String> vowel = KUtil.set(
            "A", "E", "I", "O", "U", "Å", "Ä", "Ö", "Æ", "Ø",
            "a", "e", "i", "o", "u", "å", "ä", "ö", "æ", "ø"
    );
    private static final Pattern PATTERN_UPPERCASE_ZEROWIDTH = Pattern.compile("(?=[A-Z])"); // NOTE: Use camelsplit instead for Java 6/7 compatibility.
    protected static Pattern PATTERN_ENUM_SPLIT = Pattern.compile("[\\s_]+");

    @Contract("null -> null; !null -> !null")
    public static String upperCaseFirst(String string) {
        if (string == null) return null;
        if (string.isEmpty()) return string;
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    @Contract("null -> null; !null -> !null")
    public static String lowerCaseFirst(String string) {
        if (string == null) return null;
        if (string.isEmpty()) return string;
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    @Contract("null -> false")
    public static boolean isVowel(String str) {
        if (str == null || str.isEmpty()) return false;
        return vowel.contains(str.substring(0, 1));
    }

    public static @NotNull String aan(String noun) {
        return isVowel(noun) ? "an" : "a";
    }

    public static @NotNull String implode(final Object @NotNull [] list, final String glue) {
        return implode(list, glue, null);
    }

    public static @NotNull String implode(final @NotNull Collection<?> coll, final String glue) {
        return implode(coll, glue, null);
    }

    // -------------------------------------------- //
    // FILTER
    // -------------------------------------------- //

    public static @NotNull String implode(final @NotNull Collection<?> coll, final String glue, final @Nullable String format) {
        return implode(coll.toArray(new Object[0]), glue, format);
    }

    public static @NotNull String implode(final Object @NotNull [] list, final String glue, final @Nullable String format) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            Object item = list[i];
            String str = (item == null ? "NULL" : item.toString());

            if (i != 0) {
                ret.append(glue);
            }
            if (format != null) {
                ret.append(String.format(format, str));
            } else {
                ret.append(str);
            }
        }
        return ret.toString();
    }

    public static @NotNull String implodeCommaAndDot(final @NotNull Collection<?> objects, final @Nullable String format, final String comma, final String and, final String dot) {
        if (objects.isEmpty()) return "";
        if (objects.size() == 1) {
            return implode(objects, comma, format);
        }

        List<Object> ourObjects = new ArrayList<>(objects);

        String lastItem = ourObjects.getLast().toString();
        String nextToLastItem = ourObjects.get(ourObjects.size() - 2).toString();
        if (format != null) {
            lastItem = String.format(format, lastItem);
            nextToLastItem = String.format(format, nextToLastItem);
        }
        String merge = nextToLastItem + and + lastItem;
        ourObjects.set(ourObjects.size() - 2, merge);
        ourObjects.removeLast();

        return implode(ourObjects, comma, format) + dot;
    }

    public static @NotNull List<String> camelSplit(String string) {
        List<String> ret = Arrays.asList(PATTERN_UPPERCASE_ZEROWIDTH.split(string));
        // In version before Java 8 zero width matches in the beginning created a leading empty string.
        // We manually look for it and removes it to be compatible with Java 6 and 7.
        if (ret.getFirst().isEmpty()) ret = ret.subList(1, ret.size());
        return ret;
    }


    // -------------------------------------------- //
    // Paging and chrome-tools like titleize
    // -------------------------------------------- //

    public static <T> @NotNull List<T> getFiltered(@NotNull Iterable<T> elements, @NotNull Predicate<T> predicate) {
        // Create Ret
        List<T> ret = new ArrayList<>();

        // Fill Ret
        for (T element : elements) {
            if (!predicate.apply(element)) continue;
            ret.add(element);
        }

        // Return Ret
        return ret;
    }

    public static <T> @NotNull List<T> getFiltered(T @NotNull [] elements, @NotNull Predicate<T> predicate) {
        return getFiltered(Arrays.asList(elements), predicate);
    }

    public static @NotNull List<String> getStartsWithIgnoreCase(@NotNull Iterable<String> elements, String prefix) {
        return getFiltered(elements, PredicateStartsWithIgnoreCase.get(prefix));
    }

    public static @NotNull List<String> getStartsWithIgnoreCase(String @NotNull [] elements, String prefix) {
        return getStartsWithIgnoreCase(Arrays.asList(elements), prefix);
    }

    // -------------------------------------------- //
    // Material name tools
    // -------------------------------------------- //

    public static @NotNull String getNicedEnumString(@NotNull String str, String glue) {
        List<String> parts = new ArrayList<>();
        for (String part : PATTERN_ENUM_SPLIT.split(str.toLowerCase())) {
            parts.add(upperCaseFirst(part));
        }
        return implode(parts, glue);
    }

    public static @NotNull String getNicedEnumString(@NotNull String str) {
        return getNicedEnumString(str, "");
    }

    public static <T extends Enum<T>> @NotNull String getNicedEnum(@NotNull T enumObject, String glue) {
        return getNicedEnumString(enumObject.name(), glue);
    }


    public static <T extends Enum<T>> @NotNull String getNicedEnum(@NotNull T enumObject) {
        return getNicedEnumString(enumObject.name());
    }

    public static @NotNull String getMaterialName(@NotNull Material material) {
        return getNicedEnum(material, " ");
    }

    /**
     * Determines the correct visible name of an item, based on its data (custom name and enchantment glint).<br>
     * Uses a default name of "<gray><italic>Nothing" if the item is null or empty.<br>
     * See also {@link Txt#getItemName(ItemStack, VersionedComponent)} for supplying your own default name.
     *
     * @return The name of the item, or a default name if the item is null or empty.
     */
    public static @NotNull VersionedComponent getItemName(@Nullable ItemStack itemStack) {
        return getItemName(itemStack, NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<gray><italic>Nothing"));
    }

    /**
     * Determines the correct visible name of an item, based on its data (custom name and enchantment glint).<br>
     * Uses the supplied {@param defaultName} if the item is null or empty.
     *
     * @return The name of the item, or the supplied default name if the item is null or empty.
     */
    public static @NotNull VersionedComponent getItemName(@Nullable ItemStack itemStack, @NotNull VersionedComponent defaultName) {
        if (KUtil.isNothing(itemStack)) return defaultName;
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        String color = (!itemStack.getEnchantments().isEmpty()) ? "<aqua>" : "<white>";

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                VersionedComponent prefix = serializer.fromMiniMessage(color + "<italic>");
                return prefix.append(serializer.fromLegacySection(itemMeta.getDisplayName()));
            }
        }

        return serializer.fromMiniMessage(color + getMaterialName(itemStack.getType()));
    }

    // -------------------------------------------- //
    // Tokenization
    // -------------------------------------------- //

    public static @NotNull List<String> tokenizeArguments(@NotNull String str) {
        List<String> ret = new ArrayList<>();
        StringBuilder token = null;
        boolean escaping = false;
        boolean citing = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (token == null) {
                token = new StringBuilder();
            }

            if (escaping) {
                escaping = false;
                token.append(c);
            } else if (c == '\\') {
                escaping = true;
            } else if (c == '"') {
                if (citing || !token.isEmpty()) {
                    ret.add(token.toString());
                    token = null;
                }
                citing = !citing;
            } else if (!citing && c == ' ') {
                if (!token.isEmpty()) {
                    ret.add(token.toString());
                    token = null;
                }
            } else {
                token.append(c);
            }
        }

        if (token != null) {
            ret.add(token.toString());
        }

        return ret;
    }


    // -------------------------------------------- //
    // "SMART" QUOTES
    // -------------------------------------------- //
    // The quite stupid "Smart quotes" design idea means replacing normal characters with mutated UTF-8 alternatives.
    // The normal characters look good in Minecraft.
    // The UFT-8 "smart" alternatives look quite bad.
    // http://www.fileformat.info/info/unicode/block/general_punctuation/list.htm

    @Contract("null -> null; !null -> !null")
    public static String removeSmartQuotes(String string) {
        if (string == null) return null;

        // LEFT SINGLE QUOTATION MARK
        string = string.replace("\u2018", "'");

        // RIGHT SINGLE QUOTATION MARK
        string = string.replace("\u2019", "'");

        // LEFT DOUBLE QUOTATION MARK
        string = string.replace("\u201C", "\"");

        // RIGHT DOUBLE QUOTATION MARK
        string = string.replace("\u201D", "\"");

        // ONE DOT LEADER
        string = string.replace("\u2024", ".");

        // TWO DOT LEADER
        string = string.replace("\u2025", "..");

        // HORIZONTAL ELLIPSIS
        string = string.replace("\u2026", "...");

        return string;
    }

    // -------------------------------------------- //
    // Title-ing Utils
    // -------------------------------------------- //

    /**
     * Formats a given title to be centered in a titleized line.<br><br>
     * Titleized lines can be configured in {@link Txt.Config}<br><br>
     * They feature the {@param title} centered in additional 'titleized' line characters.<br>
     *
     * @param titleMini The unformatted title, which will be titleized (still in MiniMessage format).
     *
     * @return The titleized page title as a {@link VersionedComponent}.
     */
    @NotNull
    public static VersionedComponent titleize(@NotNull String titleMini) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        // Create the title string
        String pageTitleMini = Config.getTitleFormatMini().replace(Config.getPlaceholderTitle(), titleMini);

        // Calculate the length of what will be visible (Strip colors)
        int pageTitleLength = serializer.fromMiniMessage(pageTitleMini).serializePlainText().length();

        // Calculate how many characters we need to add on either side
        int leftPaddingSize = Math.max(0, (int) Math.ceil((TITLE_LINE_LENGTH - pageTitleLength) / 2.0));
        int rightPaddingSize = Math.max(0, TITLE_LINE_LENGTH - pageTitleLength - leftPaddingSize);

        // Create the title line with padding
        String leftPaddingMini = Txt.Config.getTitlePaddingColorMini() + Txt.Config.getTitlePaddingChar().toString().repeat(leftPaddingSize);
        String rightPaddingMini = Txt.Config.getTitlePaddingColorMini() + Txt.Config.getTitlePaddingChar().toString().repeat(rightPaddingSize);

        // Construct the final title line (adds padding to both sides)
        return serializer.fromMiniMessage(leftPaddingMini + pageTitleMini + rightPaddingMini);
    }

    /**
     * Forms a page title using {@param title}, {@param pageNum} and {@param pageCount}, and then passes
     * it to {@link Txt#titleize(String)}.<br><br>
     *
     * Default format is configured in {@link Config#pageTitleFormatMini} and looks like:<br>
     *
     * @param title The unformatted title, which will be titleized.
     * @param pageNum The current page number, 1-based (e.g. 1 for the first page).
     * @param pageCount The total number of pages.
     *
     * @return The titleized page title String.
     */
    @NotNull
    public static VersionedComponent titleizedPageTitle(
            @NotNull String title,
            int pageNum,
            int pageCount,
            @NotNull List<String> args
    ) {
        String pageTitleMini = String.format(Config.getPageTitleFormatMini(), title, pageNum, pageCount);
        return Txt.titleize(pageTitleMini);
    }

    @Configurable
    public static class Config {
        /**
         * Length of a full titleized title (default: 52 characters)
         */
        public static int TITLE_LINE_LENGTH = 52;
        @Getter @Setter
        private static @NotNull Character titlePaddingChar = '_';
        @Getter @Setter
        private static @NotNull String titlePaddingColorMini = "<gold>";

        // Placeholders
        @Getter private static final @NotNull String placeholderTitle = "{title}";

        // Configurable values
        @Getter @Setter
        private static @NotNull String titleFormatMini = "<gold>.[ <dark_green>" + placeholderTitle + "<gold> ].";

        /**
         * See {@link Txt#titleizedPageTitle(String, int, int, List)} for information.
         */
        @Getter @Setter
        private static @NotNull String pageTitleFormatMini = "%s <gold>%d/%d";
    }
}
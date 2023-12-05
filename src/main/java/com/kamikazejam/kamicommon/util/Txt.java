package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommandHelp;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.mson.Mson;
import com.kamikazejam.kamicommon.util.mson.MsonEvent;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import com.kamikazejam.kamicommon.util.predicate.PredicateStartsWithIgnoreCase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kamikazejam.kamicommon.util.mson.Mson.mson;

@SuppressWarnings({"unused", "UnnecessaryUnicodeEscape"})
public class Txt {

    public static final int PAGEHEIGHT_PLAYER = 9;
    public static final int PAGEHEIGHT_CONSOLE = 50;

    public static final Map<String, String> parseReplacements;
    public static final Pattern parsePattern;

    public static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s+");
    public static final Pattern PATTERN_NEWLINE = Pattern.compile("\\r?\\n");
    public static final Pattern PATTERN_NUMBER = Pattern.compile("[0-9]+");
    private static final Pattern PATTERN_UPPERCASE_ZEROWIDTH = Pattern.compile("(?=[A-Z])"); // NOTE: Use camelsplit instead for Java 6/7 compatibility.

    public static final long millisPerSecond = 1000;
    public static final long millisPerMinute = 60 * millisPerSecond;
    public static final long millisPerHour = 60 * millisPerMinute;
    public static final long millisPerDay = 24 * millisPerHour;
    public static final long millisPerWeek = 7 * millisPerDay;
    public static final long millisPerMonth = 31 * millisPerDay;
    public static final long millisPerYear = 365 * millisPerDay;

    static {
        // Create the parse replacements map
        parseReplacements = new HashMap<>();

        // Color by name
        parseReplacements.put("<empty>", "");
        parseReplacements.put("<black>", "\u00A70");
        parseReplacements.put("<navy>", "\u00A71");
        parseReplacements.put("<green>", "\u00A72");
        parseReplacements.put("<teal>", "\u00A73");
        parseReplacements.put("<red>", "\u00A74");
        parseReplacements.put("<purple>", "\u00A75");
        parseReplacements.put("<gold>", "\u00A76");
        parseReplacements.put("<orange>", "\u00A76");
        parseReplacements.put("<silver>", "\u00A77");
        parseReplacements.put("<gray>", "\u00A78");
        parseReplacements.put("<grey>", "\u00A78");
        parseReplacements.put("<blue>", "\u00A79");
        parseReplacements.put("<lime>", "\u00A7a");
        parseReplacements.put("<aqua>", "\u00A7b");
        parseReplacements.put("<rose>", "\u00A7c");
        parseReplacements.put("<pink>", "\u00A7d");
        parseReplacements.put("<yellow>", "\u00A7e");
        parseReplacements.put("<white>", "\u00A7f");
        parseReplacements.put("<magic>", "\u00A7k");
        parseReplacements.put("<bold>", "\u00A7l");
        parseReplacements.put("<strong>", "\u00A7l");
        parseReplacements.put("<strike>", "\u00A7m");
        parseReplacements.put("<strikethrough>", "\u00A7m");
        parseReplacements.put("<under>", "\u00A7n");
        parseReplacements.put("<underline>", "\u00A7n");
        parseReplacements.put("<italic>", "\u00A7o");
        parseReplacements.put("<em>", "\u00A7o");
        parseReplacements.put("<reset>", "\u00A7r");

        // Color by semantic functionality
        parseReplacements.put("<l>", "\u00A72");
        parseReplacements.put("<logo>", "\u00A72");
        parseReplacements.put("<a>", "\u00A76");
        parseReplacements.put("<art>", "\u00A76");
        parseReplacements.put("<n>", "\u00A77");
        parseReplacements.put("<notice>", "\u00A77");
        parseReplacements.put("<i>", "\u00A7e");
        parseReplacements.put("<info>", "\u00A7e");
        parseReplacements.put("<g>", "\u00A7a");
        parseReplacements.put("<good>", "\u00A7a");
        parseReplacements.put("<b>", "\u00A7c");
        parseReplacements.put("<bad>", "\u00A7c");

        parseReplacements.put("<k>", "\u00A7b");
        parseReplacements.put("<key>", "\u00A7b");

        parseReplacements.put("<v>", "\u00A7d");
        parseReplacements.put("<value>", "\u00A7d");
        parseReplacements.put("<h>", "\u00A7d");
        parseReplacements.put("<highlight>", "\u00A7d");

        parseReplacements.put("<c>", "\u00A7b");
        parseReplacements.put("<command>", "\u00A7b");
        parseReplacements.put("<p>", "\u00A73");
        parseReplacements.put("<parameter>", "\u00A73");
        parseReplacements.put("&&", "&");
        parseReplacements.put("§§", "§");

        // Color by number/char
        for (int i = 48; i <= 122; i++) {
            char c = (char) i;
            parseReplacements.put("§" + c, "\u00A7" + c);
            parseReplacements.put("&" + c, "\u00A7" + c);
            if (i == 57) i = 96;
        }

        // Build the parse pattern and compile it
        StringBuilder patternStringBuilder = new StringBuilder();
        for (String find : parseReplacements.keySet()) {
            patternStringBuilder.append('(');
            patternStringBuilder.append(Pattern.quote(find));
            patternStringBuilder.append(")|");
        }
        String patternString = patternStringBuilder.toString();
        patternString = patternString.substring(0, patternString.length() - 1); // Remove the last |
        parsePattern = Pattern.compile(patternString);
    }


    @Contract("null -> null; !null -> !null")
    public static String upperCaseFirst(String string) {
        if (string == null) return null;
        if (string.isEmpty()) return string;
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static @NotNull String repeat(@Nullable String string, int times) {
        // Create Ret
        StringBuilder ret = new StringBuilder(times);

        // Fill Ret
        for (int i = 0; i < times; i++) {
            ret.append(string);
        }

        // Return Ret
        return ret.toString();
    }

    @Contract("null -> null; !null -> !null")
    public static String lowerCaseFirst(String string) {
        if (string == null) return null;
        if (string.isEmpty()) return string;
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public static final Set<String> vowel = KUtil.set(
            "A", "E", "I", "O", "U", "Å", "Ä", "Ö", "Æ", "Ø",
            "a", "e", "i", "o", "u", "å", "ä", "ö", "æ", "ø"
    );

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

        String lastItem = ourObjects.get(ourObjects.size() - 1).toString();
        String nextToLastItem = ourObjects.get(ourObjects.size() - 2).toString();
        if (format != null) {
            lastItem = String.format(format, lastItem);
            nextToLastItem = String.format(format, nextToLastItem);
        }
        String merge = nextToLastItem + and + lastItem;
        ourObjects.set(ourObjects.size() - 2, merge);
        ourObjects.remove(ourObjects.size() - 1);

        return implode(ourObjects, comma, format) + dot;
    }

    public static @NotNull List<String> camelsplit(String string) {
        List<String> ret = Arrays.asList(PATTERN_UPPERCASE_ZEROWIDTH.split(string));
        // In version before Java 8 zero width matches in the beginning created a leading empty string.
        // We manually look for it and removes it to be compatible with Java 6 and 7.
        if (ret.get(0).isEmpty()) ret = ret.subList(1, ret.size());
        return ret;
    }

    // -------------------------------------------- //
    // PARSE
    // -------------------------------------------- //

    @Contract("null -> null; !null -> !null")
    public static String parse(String string) {
        if (string == null) return null;
        StringBuffer ret = new StringBuffer(); // Use StringBuffer instead of StringBuilder

        Matcher matcher = parsePattern.matcher(string);
        while (matcher.find()) {
            String replacement = parseReplacements.get(matcher.group(0));
            if (replacement != null) {
                matcher.appendReplacement(ret, replacement);
            } else {
                // Handle cases where no replacement is found
                // You can append the original match or an empty string as needed
                matcher.appendReplacement(ret, "");
                // Or, throw an exception if no replacement is found
                // throw new IllegalArgumentException("No replacement found for: " + matcher.group(0));
            }
        }

        matcher.appendTail(ret);
        return ret.toString();
    }


    public static String parse(String string, Object... args) {
        return String.format(parse(string), args);
    }

    public static @NotNull ArrayList<String> parse(@NotNull Collection<String> strings) {
        ArrayList<String> ret = new ArrayList<>(strings.size());
        for (String string : strings) {
            ret.add(parse(string));
        }
        return ret;
    }


    // -------------------------------------------- //
    // FILTER
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
    // Paging and chrome-tools like titleize
    // -------------------------------------------- //

    private final static String titleizeLine = repeat("_", 52);
    private final static int titleizeBalance = -1;

    public static @NotNull Mson titleize(Object obj) {
        Mson title = mson(obj);
        if (title.getColor() == null) title = title.color(ChatColor.DARK_GREEN);

        Mson center = mson(
                mson(".[ ").color(ChatColor.GOLD),
                title,
                mson(" ].").color(ChatColor.GOLD)
        );

        int centerlen = center.length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = (centerlen / 2) - titleizeBalance;
        int eatRight = (centerlen - eatLeft) + titleizeBalance;

        if (eatLeft < pivot)
            return mson(
                    mson(titleizeLine.substring(0, pivot - eatLeft)).color(ChatColor.GOLD),
                    center,
                    mson(titleizeLine.substring(pivot + eatRight)).color(ChatColor.GOLD)
            );
        else
            return center;
    }

    @Contract(" -> new")
    public static @NotNull Mson getMessageEmpty() {
        return mson("Sorry, no pages available.").color(ChatColor.YELLOW);
    }

    public static @NotNull Mson getMessageInvalid(int size) {
        if (size == 0) {
            return getMessageEmpty();
        } else if (size == 1) {
            return mson("Invalid, there is only one page.").color(ChatColor.RED);
        } else {
            return Mson.format("Invalid, page must be between 1 and %d.", size).color(ChatColor.RED);
        }
    }

    public static @NotNull Mson titleizeMson(Object obj, int pagecount, int pageHumanBased, @Nullable KamiCommand command, List<String> args) {
        if (command == null) {
            return titleize(mson(
                    obj,
                    Mson.SPACE,
                    mson(pageHumanBased + "/" + pagecount).color(ChatColor.GOLD)
            ));
        }

        // Math
        Mson title = mson(obj, Mson.SPACE, "[<]", String.valueOf(pageHumanBased), "/", String.valueOf(pagecount), "[>]");
        int centerlen = ".[ ".length() + ChatColor.stripColor(title.toPlain(false)).length() + " ].".length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = (centerlen / 2) - titleizeBalance;
        int eatRight = (centerlen - eatLeft) + titleizeBalance;

        // Mson
        Mson centerMson = mson(
                mson(".[ ").color(ChatColor.GOLD),
                mson(obj, Mson.SPACE).color(ChatColor.DARK_GREEN),
                getFlipSection(pagecount, pageHumanBased, args, command),
                mson(" ].").color(ChatColor.GOLD)
        );

        if (eatLeft < pivot) {
            return mson(
                    mson(titleizeLine.substring(0, pivot - eatLeft)).color(ChatColor.GOLD),
                    centerMson,
                    mson(titleizeLine.substring(pivot + eatRight)).color(ChatColor.GOLD)
            );
        } else {
            return centerMson;
        }
    }

    public static @NotNull List<Mson> getPage(@NotNull List<?> lines, int pageHumanBased, Object title) {
        return getPage(lines, pageHumanBased, title, null, null, null);
    }

    public static @NotNull List<Mson> getPage(@NotNull List<?> lines, int pageHumanBased, Object title, @Nullable CommandSender sender) {
        return getPage(lines, pageHumanBased, title, sender, null, null);
    }

    public static @NotNull List<Mson> getPage(@NotNull List<?> lines, int pageHumanBased, Object title, @NotNull KamiCommand command) {
        return getPage(lines, pageHumanBased, title, command, command.getArgs());
    }

    public static @NotNull List<Mson> getPage(@NotNull List<?> lines, int pageHumanBased, Object title, @NotNull KamiCommand command, List<String> args) {
        return getPage(lines, pageHumanBased, title, command.sender, command, args);
    }

    public static @NotNull List<Mson> getPage(@NotNull List<?> lines, int pageHumanBased, Object title, @Nullable CommandSender sender, @Nullable KamiCommand command, @Nullable List<String> args) {
        return getPage(lines, pageHumanBased, title, (sender == null || sender instanceof Player) ? Txt.PAGEHEIGHT_PLAYER : Txt.PAGEHEIGHT_CONSOLE, command, args);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull List<Mson> getPage(@NotNull List<?> lines, int pageHumanBased, Object title, int pageheight, @Nullable KamiCommand command, @Nullable List<String> args) {
        // Create Ret
        List<Mson> ret = new KamiList<>();
        int pageZeroBased = pageHumanBased - 1;
        int pagecount = (int) Math.ceil(((double) lines.size()) / pageheight);

        // Add Title
        Mson msonTitle = Txt.titleizeMson(title, pagecount, pageHumanBased, command, args);
        ret.add(msonTitle);

        // Check empty and invalid
        if (pagecount == 0) {
            ret.add(getMessageEmpty());
            return ret;
        } else if (pageZeroBased < 0 || pageHumanBased > pagecount) {
            ret.add(getMessageInvalid(pagecount));
            return ret;
        }

        // Get Lines
        int from = pageZeroBased * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }

        // Check object type and add lines
        Object first = lines.get(0);

        if (first instanceof String) {
            for (String line : (List<String>) lines.subList(from, to)) {
                ret.add(Mson.fromParsedMessage(line));
            }
        } else if (first instanceof Mson) {
            ret.addAll((List<Mson>) lines.subList(from, to));
        } else {
            throw new IllegalArgumentException("The lines must be either String or Mson.");
        }

        // Return Ret
        return ret;
    }

    private static @NotNull Mson getFlipSection(int pageCount, int pageHumanBased, List<String> args, KamiCommand command) {
        // Construct Mson
        Mson start = mson(String.valueOf(pageHumanBased)).color(ChatColor.GOLD);
        Mson backward = mson("[<] ").color(ChatColor.GRAY);
        Mson forward = mson(" [>]").color(ChatColor.GRAY);
        Mson end = mson(String.valueOf(pageCount)).color(ChatColor.GOLD);

        // Set flip page backward commands
        if (pageHumanBased > 1) {
            start = setFlipPageCommand(start, pageHumanBased, 1, args, command);
            backward = setFlipPageCommand(backward, pageHumanBased, pageHumanBased - 1, args, command).color(ChatColor.AQUA);
        }

        // Set flip page forward commands
        if (pageCount > pageHumanBased) {
            forward = setFlipPageCommand(forward, pageHumanBased, pageHumanBased + 1, args, command).color(ChatColor.AQUA);
            end = setFlipPageCommand(end, pageHumanBased, pageCount, args, command);
        }

        return mson(
                backward,
                start,
                mson("/").color(ChatColor.GOLD),
                end,
                forward
        );
    }

    private static KamiCommandHelp massiveCommandHelp = null;

    public static @NotNull KamiCommandHelp getMassiveCommandHelp() {
        if (massiveCommandHelp == null) massiveCommandHelp = new KamiCommandHelp();
        return massiveCommandHelp;
    }

    private static @NotNull Mson setFlipPageCommand(@NotNull Mson mson, int pageHumanBased, int destinationPage, @Nullable List<String> args, KamiCommand command) {
        // Create the command line
        String number = String.valueOf(destinationPage);
        String oldNumber = String.valueOf(pageHumanBased);
        String commandLine;
        if (args != null) {
            int pageParamIndex = command.getPageParameterIndex();

            List<String> arguments = new ArrayList<>(args);

            // If unable to identify the page parameter, try to use the argument matching the oldNumber
            if (pageParamIndex == -1) pageParamIndex = arguments.indexOf(oldNumber);

            // If a valid index has been identified, Set the new page number there
            if (pageParamIndex > -1 && pageParamIndex < arguments.size()) arguments.set(pageParamIndex, number);

                // If unable to find valid page parameter, add the new page as a trailing argument (fallback)
            else arguments.add(number);

            commandLine = command.getCommandLine(arguments);
        } else {
            commandLine = command.getCommandLine(number);
        }

        // Render the corresponding tooltip
        String tooltip = MsonEvent.command(commandLine).createTooltip();

        // Make command line clicking
        commandLine = getMassiveCommandHelp().getCommandLine(commandLine);

        // Apply command
        mson = mson.command(commandLine);

        // Set tooltip to hide the clicking clutter
        mson = mson.tooltip(tooltip);

        // Return
        return mson;
    }

    // -------------------------------------------- //
    // Material name tools
    // -------------------------------------------- //

    protected static Pattern PATTERN_ENUM_SPLIT = Pattern.compile("[\\s_]+");

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

    public static @NotNull String getItemName(@Nullable ItemStack itemStack) {
        if (KUtil.isNothing(itemStack)) return Txt.parse("<silver><em>Nothing");

        ChatColor color = (!itemStack.getEnchantments().isEmpty()) ? ChatColor.AQUA : ChatColor.WHITE;

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                return color.toString() + ChatColor.ITALIC + itemMeta.getDisplayName();
            }
        }

        return color + Txt.getMaterialName(itemStack.getType());
    }

    public static Mson createItemMson(ItemStack item) {
        String name = Txt.getItemName(item);
        String colors = Txt.getStartColors(name);
        name = colors + "[" + ChatColor.stripColor(name) + "]";

        Mson ret = Mson.fromParsedMessage(name);

        if (!KUtil.isNothing(item)) ret = ret.item(item);

        return ret;
    }

    // -------------------------------------------- //
    // START COLORS
    // -------------------------------------------- //
    // This method never returns null

    public static final String START_COLORS_REGEX = "^((?:§.)+).*$";
    public static final Pattern START_COLORS_PATTERN = Pattern.compile(START_COLORS_REGEX);

    public static String getStartColors(String string) {
        Matcher matcher = START_COLORS_PATTERN.matcher(string);
        if (!matcher.find()) return "";
        return matcher.group(1);
    }

}

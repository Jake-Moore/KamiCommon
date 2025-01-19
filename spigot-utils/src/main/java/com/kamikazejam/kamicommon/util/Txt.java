package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.KamiCommandHelp;
import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.nms.abstraction.chat.impl.KMessageSingle;
import com.kamikazejam.kamicommon.util.collections.KamiList;
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
import java.util.regex.Pattern;

@SuppressWarnings({"UnnecessaryUnicodeEscape", "unused"})
public class Txt {

    public static final int PAGEHEIGHT_PLAYER = 9;
    public static final int PAGEHEIGHT_CONSOLE = 50;

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
    private final static String titleizeLine = "_".repeat(52);
    private final static int titleizeBalance = -1;
    protected static Pattern PATTERN_ENUM_SPLIT = Pattern.compile("[\\s_]+");
    private static KamiCommandHelp kamiCommandHelp = null;

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

    public static @NotNull String titleize(@NotNull String title) {
        // Apply color to title if there is none
        title = ChatColor.DARK_GREEN + title;

        String center = ChatColor.GOLD + ".[ " + title + ChatColor.GOLD + " ].";

        int centerLen = ChatColor.stripColor(StringUtil.t(center)).length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = (centerLen / 2) - titleizeBalance;
        int eatRight = (centerLen - eatLeft) + titleizeBalance;

        if (eatLeft < pivot) {
            return ChatColor.GOLD + titleizeLine.substring(0, pivot - eatLeft)
                    + center
                    + ChatColor.GOLD + titleizeLine.substring(pivot + eatRight);
        } else {
            return center;
        }
    }

    @Contract(" -> new")
    public static @NotNull KMessageSingle getMessageEmpty() {
        return new KMessageSingle(ChatColor.YELLOW + "Sorry, no pages available.");
    }

    public static @NotNull KMessageSingle getMessageInvalid(int size) {
        if (size == 0) {
            return getMessageEmpty();
        } else if (size == 1) {
            return new KMessageSingle(ChatColor.RED + "Invalid, there is only one page.");
        } else {
            return new KMessageSingle(ChatColor.RED + "Invalid, page must be between 1 and " + size + ".");
        }
    }

    public static @NotNull KMessageSingle titleizedPageTitle(@NotNull String title, int pageCount, int pageHumanBased, @Nullable KamiCommand command, @NotNull List<String> args) {
        if (command == null) {
            // Can't add next or back pages without a command -> just add the page numbers
            //  and skip the prev/next arrows and skip the click events
            String pageTitle = title + " " + ChatColor.GOLD + pageHumanBased + "/" + pageCount;
            return new KMessageSingle(titleize(pageTitle));
        }

        // Create the title string, using placeholders for the prev/next arrows
        String pageTitle = ChatColor.GOLD + ".[ "
                + ChatColor.DARK_GREEN + title
                + " {prevPage} "
                + ChatColor.GOLD + pageHumanBased + "/" + pageCount
                + " {nextPage}"
                + ChatColor.GOLD + " ].";

        // Calculate the length of what will be visible (Strip colors & replace variables)
        int centerLen = ChatColor.stripColor(StringUtil.t(
                pageTitle.replace("{prevPage}", "[<]").replace("{nextPage}", "[>]")
        )).length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = (centerLen / 2) - titleizeBalance;
        int eatRight = (centerLen - eatLeft) + titleizeBalance;

        KMessageSingle center = new KMessageSingle(pageTitle);
        Txt.applyPageActions(center, pageCount, pageHumanBased, args, command);

        if (eatLeft < pivot) {
            String pre = ChatColor.GOLD + titleizeLine.substring(0, pivot - eatLeft);
            String post = ChatColor.GOLD + titleizeLine.substring(pivot + eatRight);
            return center.setLine(pre + center.getLine() + post);
        } else {
            return center;
        }
    }

    public static @NotNull List<KMessage> getPage(@NotNull List<KMessageSingle> lines, int pageHumanBased, @NotNull String title, @NotNull KamiCommand command) {
        CommandContext context = command.getContext();
        Preconditions.checkNotNull(context, "Txt.getPage must be called synchronously to a command's perform execution, where CommandContext is available");
        return getPage(lines, pageHumanBased, title, (context.getSender() instanceof Player) ? Txt.PAGEHEIGHT_PLAYER : Txt.PAGEHEIGHT_CONSOLE, command, context.getArgs());
    }

    public static @NotNull List<KMessage> getPage(@NotNull List<KMessageSingle> lines, int pageHumanBased, @NotNull String title, @Nullable CommandSender sender, @Nullable KamiCommand command, @NotNull List<String> args) {
        return getPage(lines, pageHumanBased, title, (sender == null || sender instanceof Player) ? Txt.PAGEHEIGHT_PLAYER : Txt.PAGEHEIGHT_CONSOLE, command, args);
    }

    public static @NotNull List<KMessage> getPage(@NotNull List<KMessageSingle> lines, int pageHumanBased, @NotNull String title, int pageheight, @Nullable KamiCommand command, @NotNull List<String> args) {
        // Create Ret
        List<KMessage> ret = new KamiList<>();
        int pageZeroBased = pageHumanBased - 1;
        int pageCount = (int) Math.ceil(((double) lines.size()) / pageheight);

        // Add Title
        KMessageSingle kTitle = Txt.titleizedPageTitle(title, pageCount, pageHumanBased, command, args);
        ret.add(kTitle);

        // Check empty and invalid
        if (pageCount == 0) {
            ret.add(getMessageEmpty());
            return ret;
        } else if (pageZeroBased < 0 || pageHumanBased > pageCount) {
            ret.add(getMessageInvalid(pageCount));
            return ret;
        }

        // Get Lines
        int from = pageZeroBased * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }

        // Add page lines
        ret.addAll(lines.subList(from, to));

        // Return Ret
        return ret;
    }

    private static void applyPageActions(@NotNull KMessageSingle title, int pageCount, int pageHumanBased, @NotNull List<String> args, @NotNull KamiCommand command) {
        // Construct Mson
        String backward = "[<]";
        String forward = "[>]";

        // Add flip backwards command
        @Nullable String forwardCmd = getFlipPageCommand(pageHumanBased, pageHumanBased - 1, args, command);
        if (pageHumanBased > 1 && forwardCmd != null) {
            String replacement = ChatColor.AQUA + backward;
            title.addClickRunCommand("{prevPage}", replacement, forwardCmd);
        } else {
            title.setLine(title.getLine().replace("{prevPage}", ChatColor.GRAY + backward));
        }

        // Add flip forwards command
        @Nullable String backCmd = getFlipPageCommand(pageHumanBased, pageHumanBased + 1, args, command);
        if (pageCount > pageHumanBased && backCmd != null) {
            String replacement = ChatColor.AQUA + forward;
            title.addClickRunCommand("{nextPage}", replacement, backCmd);
        } else {
            title.setLine(title.getLine().replace("{nextPage}", ChatColor.GRAY + forward));
        }
    }

    public static @NotNull KamiCommandHelp getKamiCommandHelp() {
        if (kamiCommandHelp == null) kamiCommandHelp = new KamiCommandHelp();
        return kamiCommandHelp;
    }

    // -------------------------------------------- //
    // Material name tools
    // -------------------------------------------- //

    @Nullable
    private static String getFlipPageCommand(int pageHumanBased, int destinationPage, @NotNull List<String> args, @NotNull KamiCommand command) {
        // Create the command line
        String number = String.valueOf(destinationPage);

        int pageParamIndex = command.getPageParameterIndex();
        if (pageParamIndex == -1) { return null; } // Couldn't find which arg is the page

        List<String> arguments = new ArrayList<>(args);

        // If our page index is farther out than the args we've supplied so far,
        //  try supplementing with the defaults
        if (arguments.size() <= pageParamIndex) {
            // Add defaults for previous arguments
            for (int i = arguments.size(); i < pageParamIndex; i++) {
                try {
                    // Ensure we fetch a valid param, which has its default value set
                    Parameter<?> param = command.getParameter(i);
                    if (param == null || !param.isDefaultValueSet()) { return null; }
                    // Add the default value (which we know was set)
                    arguments.add(String.valueOf(command.getParameter(i).getDefaultValue()));
                }catch (IndexOutOfBoundsException ignored) {
                    return null;
                }
            }
            // Add this page number as the next argument
            arguments.add(number);
        }else {
            // The page is in the current arguments, just update it
            arguments.set(pageParamIndex, number);
        }

        return command.getCommandLine(arguments);
    }

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
        if (KUtil.isNothing(itemStack)) return StringUtil.t("&7&oNothing");

        ChatColor color = (!itemStack.getEnchantments().isEmpty()) ? ChatColor.AQUA : ChatColor.WHITE;

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                return color.toString() + ChatColor.ITALIC + itemMeta.getDisplayName();
            }
        }

        return color + Txt.getMaterialName(itemStack.getType());
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
}

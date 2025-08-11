package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.ReflectionUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.collections.KamiTreeSet;
import com.kamikazejam.kamicommon.util.comparator.ComparatorCaseInsensitive;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@SuppressWarnings({"unused"})
public abstract class TypeAbstractChoice<T> extends TypeAbstract<T> implements AllAble<T> {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected int listCountMax = 50;

    public TypeAbstractChoice<T> setListCountMax(int listCountMax) {
        this.listCountMax = listCountMax;
        return this;
    }

    protected String help = null;

    public TypeAbstractChoice<T> setHelp(String help) {
        this.help = help;
        return this;
    }

    @Setter
    protected boolean canSeeOverridden = calcCanSeeOverriden();

    public boolean calcCanSeeOverriden() {
        return !TypeAbstractChoice.class.equals(ReflectionUtil.getSuperclassDeclaringMethod(this.getClass(), true, "canSee"));
    }

    // -------------------------------------------- //
    // FIELDS: CACHE
    // -------------------------------------------- //

    // All: You should either setAll or override getAll.
    protected Collection<T> all = null;

    public void setAll(Collection<T> all) {
        if (all != null) all = Collections.unmodifiableCollection(new KamiList<>(all));
        this.all = all;

        if (all == null) {
            this.options = null;
            this.tabs = null;
        } else if (!this.isCanSeeOverridden()) {
            // The "all" cache is set and canSee is not overriden.
            // This means we can cache options and tabs.
            this.options = this.createOptions(all);
            this.tabs = this.createTabs((CommandSender) null);
        }
    }

    @SafeVarargs
    public final void setAll(T @NotNull ... all) {
        this.setAll(Arrays.asList(all));
    }

    // Options
    protected Map<String, T> options = null;

    @Contract(mutates = "this")
    public void setOptions(Map<String, T> options) {
        this.options = options;
    }

    // Tabs
    protected Collection<String> tabs = null;

    @Contract(mutates = "this")
    public void setTabs(Collection<String> tabs) {
        this.tabs = tabs;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public TypeAbstractChoice(Class<?> clazz) {
        super(clazz);
    }

    // -------------------------------------------- //
    // OVERRIDE: TYPE
    // -------------------------------------------- //

    @Override
    public T read(String arg, CommandSender sender) throws KamiCommonException {
        // NPE Evade
        if (arg == null) return null;

        // Exact
        T exact = this.getExactMatch(arg);
        if (exact != null) return exact;

        // Get All
        Collection<T> all = this.getAll(sender);

        // Get Options
        Map<String, T> options = this.getOptions();
        if (options == null) options = this.createOptions(all);

        // Get Matches
        List<T> matches = this.getMatches(options, arg, false);

        // Exact
        if (matches.size() == 1) return matches.getFirst();

        // Exception
        KamiCommonException exception = new KamiCommonException();

        // Suggestions
        boolean suggestNone = false;
        boolean suggestAmbiguous = false;
        boolean suggestAll = false;

        // Nothing Found
        String message;
        if (matches.isEmpty()) {
            message = String.format(Lang.getMessageMatchNothing(), this.getName(), arg);
            exception.addMsg(message);
        }
        // Ambiguous
        else {
            message = String.format(Lang.getMessageMatchAmbiguous(), matches.size(), this.getName(), arg);
            exception.addMsg(message);
            suggestAmbiguous = true;
        }

        // Suggest
        if (all.isEmpty()) suggestNone = true;
        if (all.size() <= this.getListCountMax()) suggestAll = true;

        if (this.canList(sender)) {
            if (suggestNone) {
                message = String.format(Lang.getMessageAvailableEmpty(), this.getName());
                exception.addMsg(message);
            } else {
                Collection<T> suggestions;

                if (suggestAmbiguous) {
                    suggestions = matches;
                    message = Lang.getMessageColonAmbiguous();
                } else if (suggestAll) {
                    suggestions = all;
                    message = Lang.getMessageColonAll();
                } else {
                    suggestions = this.getMatches(options, arg, true);
                    message = Lang.getMessageColonSimilar();
                }

                if (suggestions.isEmpty()) {
                    exception.addMsg(Lang.getMessageSuggestionsEmpty());
                } else if (suggestions.size() > this.getListCountMax()) {
                    message = String.format(Lang.getMessageSuggestionsMuch(), this.getListCountMax());
                    exception.addMsg(message);
                } else {
                    List<String> visuals = new KamiList<>();
                    for (T value : suggestions) {
                        String name = this.getName(value);
                        if (name == null) continue;
                        visuals.add(name);
                    }
                    String explode = Txt.implodeCommaAndDot(
                            visuals,
                            KamiCommand.Lang.getErrorParamColor() + "%s",
                            " " + ChatColor.GRAY + "| ",
                            " " + ChatColor.GRAY + "| ",
                            ""
                    );
                    exception.addMsg(message + explode);
                }
            }
        }

        // Help
        String help = this.getHelp();
        if (help != null) exception.addMsg(help);

        throw exception;
    }

    // -------------------------------------------- //
    // OVERRIDE: ALL ABLE
    // -------------------------------------------- //

    @Override
    public Collection<T> getAll(CommandSender sender) {
        // No Can See Override?
        if (!this.isCanSeeOverridden()) return this.getAll();

        // Create
        Set<T> ret = new KamiSet<>();

        // Fill
        for (T value : this.getAll()) {
            if (!this.canSee(value, sender)) continue;
            ret.add(value);
        }

        // Return
        return ret;
    }

    public boolean canList(CommandSender sender) {
        return true;
    }

    public boolean canSee(T value, CommandSender sender) {
        return true;
    }

    // -------------------------------------------- //
    // MATCHES
    // -------------------------------------------- //

    public List<T> getMatches(@NotNull Map<String, T> options, String arg, boolean levenshtein) {
        // Create
        List<T> ret = new KamiList<>();

        // Prepare
        arg = this.prepareOptionKey(arg);

        // Exact
        T exact = options.get(arg);
        if (exact != null) return Collections.singletonList(exact);

        // Fill
        for (Map.Entry<String, T> entry : options.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();

            if (levenshtein) {
                if (!isLevenshteinSimilar(arg, key)) continue;
            } else {
                if (!key.startsWith(arg)) continue;
            }

            ret.add(value);
        }

        // Return
        return ret;
    }

    // Can be overridden to make use of existing indexes.
    public T getExactMatch(String arg) {
        return null;
    }

    public boolean isLevenshteinSimilar(String arg, String string) {
        int max = this.getLevenshteinMax(arg);
        return LevenshteinDistance.getDefaultInstance().apply(arg, string) <= max;
    }

    public int getLevenshteinMax(String arg) {
        if (arg == null) return 0; // For some apparent reason this is required.
        if (arg.length() <= 1) return 0; // When dealing with 1 character aliases, there is way too many options.
        if (arg.length() <= 7) return 1; // 1 is default.

        return 2;  // If it were 8 characters or more, we end up here. Because many characters allow for more typos.
    }

    // -------------------------------------------- //
    // OPTIONS
    // -------------------------------------------- //

    public Map<String, T> createOptions(@NotNull Iterable<T> all) {
        // Create
        Map<String, T> ret = new KamiMap<>();

        // Fill
        for (T value : all) {
            for (String key : this.createOptionKeys(value)) {
                ret.put(key, value);
            }
        }

        // Return
        return ret;
    }

    // This method creates keys for a certain value.
    // They ARE comparable.
    public List<String> createOptionKeys(T value) {
        // Create
        List<String> ret = new KamiList<>();

        // Fill
        String string;

		for (String name : this.getNames(value)) {
			string = this.prepareOptionKey(name);
			if (string != null) ret.add(string);
		}

		for (String id : this.getIds(value)) {
			string = this.prepareOptionKey(id);
			if (string != null) ret.add(string);
		}

        // Return
        return ret;
    }

    // The purpose of this method is to strip down a string to a comparable string key.
    protected static Pattern PATTERN_KEY_UNWANTED = Pattern.compile("[_\\-\\s]+");

    public String prepareOptionKey(String string) {
        if (string == null) return null;
        string = string.trim();
        string = string.toLowerCase();
        // SLOW: string = string.replaceAll("[_\\-\\s]+", "");
        string = PATTERN_KEY_UNWANTED.matcher(string).replaceAll("");
        return string;
    }

    // -------------------------------------------- //
    // TAB
    // -------------------------------------------- //

    @Override
    public Collection<String> getTabList(CommandSender sender, String arg) {
        Collection<String> ret = this.getTabs();
        if (ret == null) ret = this.createTabs(sender);
        return ret;
    }

    public Set<String> createTabs(CommandSender sender) {
        // Create
        Set<String> ret = new KamiSet<>();

        // Fill
        for (T value : this.getAll(sender)) {
            ret.addAll(this.createTabs(value));
        }

        // Return
        return ret;
    }

    public Set<String> createTabs(T value) {
        // Create
        Set<String> ret = new KamiTreeSet<>(ComparatorCaseInsensitive.get());

        // Fill
        String string;

        for (String name : this.getNames(value)) {
            string = this.prepareTab(name, true);
            if (string != null) ret.add(string);

            string = this.prepareTab(name, false);
            if (string != null) ret.add(string);
        }

        for (String id : this.getIds(value)) {
            string = this.prepareTab(id, true);
            if (string != null) ret.add(string);

            string = this.prepareTab(id, false);
            if (string != null) ret.add(string);
        }

        // Return
        return ret;
    }

    public String prepareTab(String string, boolean spaces) {
        if (string == null) return null;
        string = string.trim();
        if (!spaces) string = string.replace(" ", "");
        return string;
    }

    /**
     * Message and Color configuration for TypeAbstractChoice chat responses.
     */
    public static class Lang {
        // Messages
        @Setter private static @NotNull String messageMatchNothing =        KamiCommand.Lang.placeholderErrorColor + "No %s matches \"" + KamiCommand.Lang.placeholderErrorParamColor + "%s" + KamiCommand.Lang.placeholderErrorColor + "\".";
        @Setter private static @NotNull String messageMatchAmbiguous =      KamiCommand.Lang.placeholderErrorColor + "%d %ss matches \"" + KamiCommand.Lang.placeholderErrorParamColor + "%s" + KamiCommand.Lang.placeholderErrorColor + "\".";
        @Setter @Getter private static @NotNull String messageAvailableEmpty =      ChatColor.YELLOW + "Note: There is no %s available.";

        @Setter @Getter private static @NotNull String messageColonAmbiguous =      ChatColor.AQUA + "Ambiguous" + ChatColor.GRAY + ": ";
        @Setter @Getter private static @NotNull String messageColonAll =            ChatColor.AQUA + "All" + ChatColor.GRAY + ": ";
        @Setter @Getter private static @NotNull String messageColonSimilar =        ChatColor.AQUA + "Similar" + ChatColor.GRAY + ": ";

        @Setter @Getter private static @NotNull String messageSuggestionsEmpty =    ChatColor.YELLOW + "No suggestions found.";
        @Setter @Getter private static @NotNull String messageSuggestionsMuch =     ChatColor.YELLOW + "Over %d suggestions found (hiding output).";

        // Derived Getters
        public static @NotNull String getMessageMatchNothing() {
            return messageMatchNothing
                    .replace(KamiCommand.Lang.placeholderErrorColor, KamiCommand.Lang.getErrorColor().toString())
                    .replace(KamiCommand.Lang.placeholderErrorParamColor, KamiCommand.Lang.getErrorParamColor().toString());
        }
        public static @NotNull String getMessageMatchAmbiguous() {
            return messageMatchAmbiguous
                    .replace(KamiCommand.Lang.placeholderErrorColor, KamiCommand.Lang.getErrorColor().toString())
                    .replace(KamiCommand.Lang.placeholderErrorParamColor, KamiCommand.Lang.getErrorParamColor().toString());
        }
    }
}

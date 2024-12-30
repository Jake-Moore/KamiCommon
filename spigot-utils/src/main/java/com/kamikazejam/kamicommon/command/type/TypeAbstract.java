package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.ReflectionUtil;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.interfaces.Identified;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;

@SuppressWarnings({"unused"})
public abstract class TypeAbstract<T> implements Type<T> {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final String NULL = StringUtil.t("&7&oNONE");
	public static final String EMPTY = StringUtil.t("&7&oEMPTY");
	public static final String UNKNOWN = StringUtil.t("&c???");

	public static final ChatColor COLOR_DEFAULT = ChatColor.YELLOW;

	public static final int TAB_LIST_UUID_THRESHOLD = 5;

	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	@Override
	public @NotNull String getName() {
		int prefixLength = "Type".length();
		String name = this.getClass().getSimpleName();

		// We don't want the "Type" part
		name = name.substring(prefixLength);

		// We split at uppercase letters, because most class names are camel-case.
		final List<String> words = Txt.camelSplit(name);
		return Txt.implode(words, " ").toLowerCase();
	}

	@Nullable
	public String getName(@Nullable T value) {
		if (value == null) return null;
		if (value instanceof Named named) {
			return named.getName();
		}
		return this.getId(value);
	}

	@NotNull
	public Set<String> getNames(@Nullable T value) {
		if (value == null) return Collections.emptySet();
		String name = this.getName(value);
		if (name == null) return Collections.emptySet();
		return new KamiSet<>(name);
	}

	@Nullable
	public String getId(@Nullable T value) {
		if (value == null) return null;
		if (value instanceof Identified identified) {
            return identified.getId();
		} else if (value instanceof String || value instanceof Number || value instanceof Boolean) {
			return value.toString();
		}
		return null;
	}

	@NotNull
	public Set<String> getIds(@Nullable T value) {
		if (value == null) return Collections.emptySet();
		String id = this.getId(value);
		if (id == null) return Collections.emptySet();
		return new KamiSet<>(id);
	}

	@Getter
	protected final Class<T> clazz;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@SuppressWarnings("unchecked")
	public TypeAbstract(Class<?> clazz) {
		this.clazz = (Class<T>) clazz;

		try {
			constructor = ReflectionUtil.getConstructor(clazz);
		} catch (Exception ignored) {}
	}

	// -------------------------------------------- //
	// TAB LIST
	// -------------------------------------------- //

	@Override
	public final List<String> getTabListFiltered(CommandSender sender, String arg) {
		// Get the raw tab list.
		Collection<String> raw = this.getTabList(sender, arg);

		// Handle null case.
		if (raw == null || raw.isEmpty()) return Collections.emptyList();

		// Only keep the suggestions that starts with what the user already typed in.
		// This is the first basic step of tab completion.
		// "Ca" can complete into "Cayorion".
		// "Ma" can complete into "Madus"
		// "Ca" can not complete into "Madus" because it does not start with ignore case.
		List<String> ret = Txt.getStartsWithIgnoreCase(raw, arg);

		// Initial simple cleanup of suggestions.
		cleanSuggestions(ret);

		// Here we do a lot of things related to spaces.
		// Because spaces and tab completion doesn't go well together.
		// In the future we might be able to do something better, but Minecraft has its limitations.
		ret = prepareForSpaces(ret, arg);

		return ret;
	}

	// -------------------------------------------- //
	// TAB LIST > PRIVATE TAB COMPLETE CALCULATIONS
	// -------------------------------------------- //

	// This method performs an initial cleanup of suggestions.
	// Currently we just throw away nulls and empty strings.
	private static void cleanSuggestions(@NotNull List<String> suggestions) {
		suggestions.removeIf(suggestion -> suggestion == null || suggestion.isEmpty());
	}

	public static @NotNull List<String> prepareForSpaces(List<String> suggestions, String arg) {
		// This will get the common prefix for all passed in suggestions.
		// This will allow us to tab complete some things with spaces
		// if we know they all start with the same value,
		// so we don't have to replace all of it.
		final String prefix = getPrefix(suggestions);

		// This is all the suggestions without the common prefix.
		List<String> ret = withoutPreAndSuffix(suggestions, prefix);
		// If it isn't empty and there is a prefix...
		if (!ret.isEmpty() && !prefix.isEmpty()) {
			// ...then we want the first one to have the prefix.
			// That prefix is not removed automatically,
			// due to how tab completion works.
			final String current = ret.getFirst();
			String result = prefix;
			if (!current.isEmpty()) {
				if (result.charAt(result.length() - 1) != ' ') result += ' ';
				result += current;
			}

			int unwantedPrefixLength = arg.lastIndexOf(' ');
			if (unwantedPrefixLength != -1) {
				unwantedPrefixLength++;
				result = result.substring(unwantedPrefixLength);
			}
			ret.set(0, result);
		}

		return ret;
	}

	private static @NotNull String getPrefix(@NotNull List<String> suggestions) {
		String prefix = null;

		for (String suggestion : suggestions) {
			prefix = getOkay(prefix, suggestion);
		}

		if (prefix == null) return "";
		int lastSpace = prefix.lastIndexOf(" ");
		if (lastSpace == -1) return "";

		return prefix.substring(0, lastSpace + 1);
	}

	// This method return a new string only including the first characters that are equal.
	@Contract("null, _ -> param2; _, !null -> !null")
	private static String getOkay(String original, String compared) {
		if (original == null) return compared;
		final int size = Math.min(original.length(), compared.length());
		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < size; i++) {
			if (Character.toLowerCase(compared.charAt(i)) != Character.toLowerCase(original.charAt(i))) break;
			ret.append(compared.charAt(i));
		}

		if (ret.isEmpty()) return "";

		int lastSpace = ret.lastIndexOf(" ");
		if (lastSpace == -1) return "";

		return ret.toString();
	}

	@Contract("_, _ -> new")
	private static @NotNull List<String> withoutPreAndSuffix(@NotNull List<String> suggestions, String prefix) {
		KamiSet<String> ret = new KamiSet<>(suggestions.size());
		boolean includesPrefix = false; // Sometimes a suggestion is equal to the prefix.
		for (String suggestion : suggestions) {
			if (suggestion.equals(prefix) && !includesPrefix) {
				ret.add("");
				includesPrefix = true;
				continue;
			}
			// We remove the prefix because we only want that once.
			// But we can't keep things after the first part either
			// because of spaces and stuff.
			if (suggestion.length() <= prefix.length()) continue;
			int lastSpace = suggestion.indexOf(' ', prefix.length());
			int lastIndex = lastSpace != -1 ? lastSpace : suggestion.length();
			ret.add(suggestion.substring(prefix.length(), lastIndex));
		}

		return new ArrayList<>(ret);
	}

	// -------------------------------------------- //
	// EQUALS
	// -------------------------------------------- //

	@Override
	public boolean equals(T type1, T type2) {
		if (type1 == null) return type2 == null;
		if (type2 == null) return false;
		return this.equalsInner(type1, type2);
	}

	@Override
	public boolean equalsInner(@NotNull T type1, T type2) {
		return type1.equals(type2);
	}

	private Constructor<T> constructor;
	@Override
	public T createNewInstance() {
		try {
			return this.constructor.newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}

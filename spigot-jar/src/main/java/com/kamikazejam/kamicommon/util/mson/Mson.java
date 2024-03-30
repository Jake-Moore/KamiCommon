package com.kamikazejam.kamicommon.util.mson;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.adapter.AdapterLowercaseEnum;
import com.kamikazejam.kamicommon.util.adapter.AdapterMsonEventFix;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "SpellCheckingInspection", "UnnecessaryUnicodeEscape"})
public class Mson implements Serializable {
	// -------------------------------------------- //
	// CONSTANTS: TECHY
	// -------------------------------------------- //

	public static final Pattern PATTERN_PARSE_PREFIX = Pattern.compile("(?=(?<vhex>(\u00A7x(?:\u00A7[a-fA-F0-9]){6})))|(?=(?<!\u00A7x(?:\u00A7[a-fA-F0-9]){0,5})(?<code>\u00A7[0-9a-fk-or]))|(?=<(?<mhex>(#[a-fA-F0-9]{6}))>)");

	public static final AdapterLowercaseEnum<ChatColor> ADAPTER_LOWERCASE_CHAT_COLOR = AdapterLowercaseEnum.get(ChatColor.class);
	public static final AdapterLowercaseEnum<MsonEventAction> ADAPTER_LOWERCASE_MSON_EVENT_ACTION = AdapterLowercaseEnum.get(MsonEventAction.class);

	// -------------------------------------------- //
	// CONSTANTS: REUSABLE MSONS
	// -------------------------------------------- //

	public static final Mson SPACE = mson(" ");
	public static final Mson EMPTY = mson("");
	public static final Mson NEWLINE = mson("\n");
	public static final Mson DOT = mson(".");
	public static final Mson NULL = mson("NULL");
	public static final Mson SPACE_AND_SPACE = mson(" and ");
	public static final Mson COMMA_SPACE = mson(", ");

	// -------------------------------------------- //
	// GSON
	// -------------------------------------------- //
	// We need two different Gson instances that chain into each other.
	// The external one contains repairs and preprocessors.
	// The internal one is free from repairs and preprocessors.
	// This way we can avoid stack overflows.

	private static Gson GSON_EXTERNAL = null;
	private static Gson GSON_INTERNAL = null;

	public static Gson getGson(boolean external) {
		Gson ret = (external ? GSON_EXTERNAL : GSON_INTERNAL);
		if (ret == null) {
			ret = createGson(external);
			if (external) {
				GSON_EXTERNAL = ret;
			} else {
				GSON_INTERNAL = ret;
			}
		}
		return ret;
	}

	private static Gson createGson(boolean external) {
		GsonBuilder builder = new GsonBuilder();
		builder.disableHtmlEscaping();

		if (external) builder.registerTypeAdapter(MsonEvent.class, AdapterMsonEventFix.get());

		builder.registerTypeAdapter(MsonEventAction.class, ADAPTER_LOWERCASE_MSON_EVENT_ACTION);

		builder.registerTypeAdapter(ChatColor.class, ADAPTER_LOWERCASE_CHAT_COLOR);

		// For some unknown reason, the different chat colors
		// have their own instance of java.lang.Class.
		// For them to be serialized properly with gson,
		// we must specify the adapter for ALL of these classes.

		// However the adapter should be created with the base class
		// because the base class returns true on Class#isEnum
		// and returns a non-null value on Class#getEnumConstants.
		for (ChatColor color : ChatColor.values()) {
			builder.registerTypeAdapter(color.getClass(), ADAPTER_LOWERCASE_CHAT_COLOR);
		}

		return builder.create();
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	// FIELD: The Msons text
	// A parents text can't be null, then Mojang throws an exception.
	// It does not make sense for something which doesn't have extras
	// to not have text, because then it doesn't show up at all.
	private final String text;

	public @NotNull String getText() {
		return this.text;
	}

	// FIELD: Color of the mson
	private final String color;

	public @Nullable String getColor() {
		return this.color;
	}

	public @Nullable String getEffectiveColor() {
		return color != null ? color : getInheritedColor();
	}

	public @Nullable String getInheritedColor() {
		return hasParent() ? Objects.requireNonNull(getParent()).getEffectiveColor() : null;
	}

	public @Nullable ChatColor getEffectiveColorCode() {
		if (color != null) {
			try {
				if (color.startsWith("#")) {
					return KUtil.getNearestChatColor(color);
				}
				return ChatColor.valueOf(Objects.requireNonNull(getEffectiveColor()).toUpperCase());
			} catch (Exception ignored) {
			}
		}
		return getInheritedColorCode();
	}

	public ChatColor getInheritedColorCode() {
		return hasParent() ? Objects.requireNonNull(getParent()).getEffectiveColorCode() : null;
	}

	// FIELD: bold
	private final Boolean bold;

	public @Nullable Boolean isBold() {
		return bold;
	}

	public @Nullable Boolean isEffectiveBold() {
		return bold != null ? bold : isInheritedBold();
	}

	public @Nullable Boolean isInheritedBold() {
		return hasParent() ? Objects.requireNonNull(getParent()).isEffectiveBold() : null;
	}

	// FIELD: italic
	private final Boolean italic;

	public @Nullable Boolean isItalic() {
		return this.italic;
	}

	public @Nullable Boolean isEffectiveItalic() {
		return italic != null ? italic : isInheritedItalic();
	}

	protected @Nullable Boolean isInheritedItalic() {
		return hasParent() ? Objects.requireNonNull(getParent()).isEffectiveItalic() : null;
	}

	// FIELD: underlined
	private final Boolean underlined;

	public @Nullable Boolean isUnderlined() {
		return this.underlined;
	}

	public @Nullable Boolean isEffectiveUnderlined() {
		return underlined != null ? underlined : isInheritedUnderlined();
	}

	protected @Nullable Boolean isInheritedUnderlined() {
		return hasParent() ? Objects.requireNonNull(getParent()).isEffectiveUnderlined() : null;
	}

	// FIELD: strikethrough
	private final Boolean strikethrough;

	public @Nullable Boolean isStrikethrough() {
		return this.strikethrough;
	}

	public @Nullable Boolean isEffectiveStrikethrough() {
		return strikethrough != null ? strikethrough : isInheritedStrikethrough();
	}

	protected @Nullable Boolean isInheritedStrikethrough() {
		return hasParent() ? Objects.requireNonNull(getParent()).isEffectiveStrikethrough() : null;
	}

	// FIELD: obfuscated
	private final Boolean obfuscated;

	public @Nullable Boolean isObfuscated() {
		return this.obfuscated;
	}

	public @Nullable Boolean isEffectiveObfuscated() {
		return obfuscated != null ? obfuscated : isInheritedObfuscated();
	}

	protected @Nullable Boolean isInheritedObfuscated() {
		return hasParent() ? Objects.requireNonNull(getParent()).isEffectiveObfuscated() : null;
	}

	// FIELD: The Events which happen when you click, hover over or shift-click the message
	protected final MsonEvent clickEvent;
	protected final MsonEvent hoverEvent;

	public MsonEvent getEvent(@NotNull MsonEventType type) {
		return type.get(this);
	}

	public @Nullable MsonEvent getEffectiveEvent(@NotNull MsonEventType type) {
		return type.get(this) != null ? type.get(this) : getInheritedEvent(type);
	}

	protected @Nullable MsonEvent getInheritedEvent(@NotNull MsonEventType type) {
		return this.hasParent() ? Objects.requireNonNull(this.getParent()).getEffectiveEvent(type) : null;
	}

	private final String insertion;

	public @Nullable String getInsertion() {
		return this.insertion;
	}

	public @Nullable String getEffectiveInsertion() {
		return insertion != null ? insertion : getInheritedInsertion();
	}

	protected @Nullable String getInheritedInsertion() {
		return this.hasParent() ? Objects.requireNonNull(this.getParent()).getEffectiveInsertion() : null;
	}

	// The other parts of the message
	private final List<Mson> extra;

	public @Nullable List<Mson> getExtra() {
		return this.extra;
	}

	public boolean hasExtra() {
		return this.getExtra() != null;
	}

	// Parent & Root
	private final transient Mson parent;

	public @Nullable Mson getParent() {
		return this.parent;
	}

	public boolean hasParent() {
		return this.getParent() != null;
	}

	public boolean isRoot() {
		return this.getParent() == null;
	}

	public @NotNull Mson getRoot() {
		Mson root = this;
		while (true) {
			Mson parent = root.getParent();
			if (parent == null) break;
			root = parent;
		}
		return root;
	}

	// -------------------------------------------- //
	// STATE CHECKING
	// -------------------------------------------- //

	public boolean isEmpty() {
		// It has text, not empty.
		if (!this.getText().isEmpty()) return false;

		if (this.hasExtra()) {
			for (Mson extra : Objects.requireNonNull(this.getExtra())) {
				// It is empty
				if (extra.isEmpty()) continue;

				// It was not empty.
				return false;
			}
		}

		// We are empty.
		return true;
	}

	public boolean isTextOnly() {
		if (this.getColor() != null) return false;
		if (this.isBold() != null) return false;
		if (this.isItalic() != null) return false;
		if (this.isUnderlined() != null) return false;
		if (this.isStrikethrough() != null) return false;
		if (this.isObfuscated() != null) return false;
		if (this.getEvent(MsonEventType.CLICK) != null) return false;
		if (this.getEvent(MsonEventType.HOVER) != null) return false;
		if (this.getInsertion() != null) return false;
        return !this.hasExtra();
    }

	public boolean hasSpecialBehaviour() {
		if (this.getEvent(MsonEventType.CLICK) != null) return true;
		if (this.getEvent(MsonEventType.HOVER) != null) return true;
		if (this.getInsertion() != null) return true;

		if (this.hasExtra()) {
			for (Mson extra : Objects.requireNonNull(this.getExtra())) {
				if (extra.hasSpecialBehaviour()) return true;
			}
		}

		return false;
	}

	// -------------------------------------------- //
	// WITH FIELDS
	// -------------------------------------------- //

	@Contract("_ -> new")
	public @NotNull Mson text(@NotNull String text) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson color(@Nullable String color) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson color(@Nullable ChatColor color) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson bold(@Nullable Boolean bold) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson italic(@Nullable Boolean italic) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson underlined(@Nullable Boolean underlined) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson strikethrough(@Nullable Boolean strikethrough) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	@Contract("_ -> new")
	public @NotNull Mson obfuscated(@Nullable Boolean obfuscated) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	private @NotNull Mson createTooltip(Boolean override, String created) {
		if (created == null) override = false;
		if (override == null) override = (this.getTooltip() == null && this.getItem() == null);
		return override ? this.tooltip(created) : this;
	}

	public @NotNull Mson event(Boolean tooltip, @Nullable MsonEventType type, @NotNull MsonEvent event) {
		if (type == null) type = event.getType();
		Mson ret = type.set(this, event);
		String created = event.createTooltip();
		return ret.createTooltip(tooltip, created);
	}

	public @NotNull Mson event(Boolean tooltip, @NotNull MsonEvent event) {
		return this.event(tooltip, null, event);
	}

	public @NotNull Mson event(@Nullable MsonEventType type, @NotNull MsonEvent event) {
		return this.event(null, type, event);
	}

	public @NotNull Mson event(@NotNull MsonEvent event) {
		return this.event(null, null, event);
	}

	public @NotNull Mson insertionString(String insertionString, Boolean tooltip) {
		String prefix = Txt.parse("<h>Shift-Click Insert: <c>");
		Mson ret = Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertionString, extra, parent);
		return ret.createTooltip(tooltip, prefix + insertionString);
	}

	public @NotNull Mson insertionString(String insertionString) {
		return this.insertionString(insertionString, null);
	}

	public @NotNull Mson extra(Mson extra) {
		return extra(new Mson[]{extra});
	}

	public @NotNull Mson extra(@Nullable List<Mson> extra) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	public @NotNull Mson extra(Mson @Nullable [] extra) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra == null ? null : ImmutableList.copyOf(extra), parent);
	}

	public @NotNull Mson parent(@Nullable Mson parent) {
		return Mson.valueOf(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, extra, parent);
	}

	// -------------------------------------------- //
	// ADD
	// -------------------------------------------- //

	public @NotNull Mson add(Object part) {
		return this.add(new Object[]{part});
	}

	public @NotNull Mson add(Object @NotNull ... parts) {
		return this.add(Arrays.asList(parts));
	}

	public @NotNull Mson add(@NotNull Iterable<?> parts) {
		List<Mson> extra = new KamiList<>(this.getExtra());
		List<Mson> msons = msons(parts);
		extra.addAll(msons);
		return this.extra(extra);
	}

	// -------------------------------------------- //
	// CONVENIENCE MSON EVENT
	// -------------------------------------------- //

	public @NotNull Mson link(String link) {
		return this.event(MsonEvent.link(link));
	}

	public @NotNull Mson suggest(String suggest) {
		return this.event(MsonEvent.suggest(suggest));
	}

	public @NotNull Mson suggest(@NotNull KamiCommand command, String... args) {
		return this.event(MsonEvent.suggest(command, args));
	}

	public @NotNull Mson suggest(@NotNull KamiCommand command, Iterable<String> args) {
		return this.event(MsonEvent.suggest(command, args));
	}

	public @NotNull Mson command(String command) {
		return this.event(MsonEvent.command(command));
	}

	public @NotNull Mson command(@NotNull KamiCommand command, String... args) {
		return this.event(MsonEvent.command(command, args));
	}

	public @NotNull Mson command(@NotNull KamiCommand command, Iterable<String> args) {
		return this.event(MsonEvent.command(command, args));
	}

	public @NotNull Mson tooltip(String tooltip) {
		return this.event(MsonEvent.tooltip(tooltip));
	}

	public @NotNull Mson tooltip(String @NotNull ... tooltip) {
		return this.event(MsonEvent.tooltip(tooltip));
	}

	public @NotNull Mson tooltip(@NotNull Collection<String> tooltip) {
		return this.event(MsonEvent.tooltip(tooltip));
	}

	public @NotNull Mson tooltipParse(String tooltip) {
		return this.event(MsonEvent.tooltipParse(tooltip));
	}

	public @NotNull Mson tooltipParse(String @NotNull ... tooltip) {
		return this.event(MsonEvent.tooltipParse(tooltip));
	}

	public @NotNull Mson tooltipParse(@NotNull Collection<String> tooltip) {
		return this.event(MsonEvent.tooltipParse(tooltip));
	}

	public @NotNull Mson item(@NotNull ItemStack item) {
		return this.event(MsonEvent.item(item));
	}

	public @Nullable String getLink() {
		return this.getEventValue(MsonEventAction.OPEN_URL);
	}

	public @Nullable String getSuggest() {
		return this.getEventValue(MsonEventAction.SUGGEST_COMMAND);
	}

	public @Nullable String getCommand() {
		return this.getEventValue(MsonEventAction.RUN_COMMAND);
	}

	public @Nullable String getTooltip() {
		return this.getEventValue(MsonEventAction.SHOW_TEXT);
	}

	public @Nullable String getItem() {
		return this.getEventValue(MsonEventAction.SHOW_ITEM);
	}

	protected @Nullable String getEventValue(@NotNull MsonEventAction targetAction) {
		MsonEventType type = targetAction.getType();

		MsonEvent event = this.getEvent(type);
		if (event == null) return null;

		MsonEventAction action = event.getAction();
		if (action == null) return null;

		if (action != targetAction) return null;

		return event.getValue();
	}

	// -------------------------------------------- //
	// CONVENIENCE STYLE
	// -------------------------------------------- //

	public @NotNull Mson style(ChatColor @NotNull ... styles) {
		Mson ret = this;
		for (ChatColor style : styles) {
			ret = ret.style(style);
		}
		return ret;
	}

	@Contract("null -> fail")
	public @NotNull Mson style(ChatColor style) {
		if (style == null) throw new NullPointerException("style");

		if (style == ChatColor.RESET) return this.removeStyles();
		if (style == ChatColor.BOLD) return this.bold(true);
		if (style == ChatColor.ITALIC) return this.italic(true);
		if (style == ChatColor.UNDERLINE) return this.underlined(true);
		if (style == ChatColor.STRIKETHROUGH) return this.strikethrough(true);
		if (style == ChatColor.MAGIC) return this.obfuscated(true);
		if (style.isColor()) return this.color(style.name().toLowerCase());

		throw new UnsupportedOperationException(style.name());
	}

	@Contract(pure = true)
	public @NotNull Mson removeStyles() {
		// NOTE: We can't use null.
		// Since we want to override color and format in parents.
		return Mson.valueOf(text, ChatColor.WHITE, false, false, false, false, false, clickEvent, hoverEvent, insertion, extra, parent);
	}

	public @NotNull Mson stripStyle() {
		Mson ret = Mson.valueOf(text, (String) null, null, null, null, null, null, clickEvent, hoverEvent, insertion, null, parent);

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				extra[i] = part.stripStyle();
				i++;
			}
			ret = ret.extra(extra);
		}

		return ret;
	}

	// This will set all style and behaviour to the effective value.
	// So parents won't affect this.
	public @NotNull Mson enforced() {
		return valueOf(
				this.getText(),
				this.getEffectiveColor(),
				this.isEffectiveBold(),
				this.isEffectiveItalic(),
				this.isEffectiveUnderlined(),
				this.isEffectiveStrikethrough(),
				this.isEffectiveObfuscated(),
				this.getEffectiveEvent(MsonEventType.CLICK),
				this.getEffectiveEvent(MsonEventType.HOVER),
				this.getEffectiveInsertion(),
				this.getExtra(),
				null
		);
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	// Empty
	@Contract(pure = true)
	public static @NotNull Mson mson() {
		return EMPTY;
	}

	// Text
	Mson(@NotNull String text) {
		this(text, (String) null, null, null, null, null, null, null, null, null, null, null);
	}

	@Contract("_ -> new")
	public static @NotNull Mson mson(@NotNull String text) {
		return new Mson(text);
	}

	// Full
	Mson(@NotNull String text,
		 @Nullable String color,
		 @Nullable Boolean bold,
		 @Nullable Boolean italic,
		 @Nullable Boolean underlined,
		 @Nullable Boolean strikethrough,
		 @Nullable Boolean obfuscated,
		 @Nullable MsonEvent clickEvent,
		 @Nullable MsonEvent hoverEvent,
		 @Nullable String insertionString,
		 @Nullable List<Mson> extra,
		 @Nullable Mson parent) {
		// Text
		this.text = Objects.requireNonNull(text);

		// Color
		if (color != null) {
			if (!color.startsWith("#")) {
				ChatColor chatColor = ChatColor.valueOf(color.toUpperCase());
				if (!chatColor.isColor()) throw new IllegalArgumentException(chatColor.name() + " is not a color");
			}
			color = color.toLowerCase();
		}
		this.color = color;

		// Format
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;

		// Set Events
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;

		// Validate Events
		MsonEventType type;
		MsonEvent event;

		type = MsonEventType.CLICK;
		event = this.getEvent(type);
		if (event != null && event.getType() != type)
			throw new IllegalArgumentException(event.getAction().name() + " is not of type " + type);

		type = MsonEventType.HOVER;
		event = this.getEvent(type);
		if (event != null && event.getType() != type)
			throw new IllegalArgumentException(event.getAction().name() + " is not of type " + type);


		// Insertionstring
		this.insertion = insertionString;

		// Mojang doesn't allow zero sized arrays, but null is fine. So null.
		if (extra != null && extra.isEmpty()) extra = null;

		// Extra
		if (extra != null) {
			Mson[] extras = new Mson[extra.size()];
			for (ListIterator<Mson> it = extra.listIterator(); it.hasNext(); ) {
				int i = it.nextIndex();
				Mson part = it.next();
				extras[i] = part.parent(this);
			}
			// Copy extras into a list
			this.extra = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(extras)));
		} else {
			this.extra = null;
		}

		// Parent
		if (this == parent) throw new IllegalArgumentException("Parent can't be oneself.");
		this.parent = parent;
	}

	Mson(@NotNull String text,
		 @Nullable ChatColor color,
		 @Nullable Boolean bold,
		 @Nullable Boolean italic,
		 @Nullable Boolean underlined,
		 @Nullable Boolean strikethrough,
		 @Nullable Boolean obfuscated,
		 @Nullable MsonEvent clickEvent,
		 @Nullable MsonEvent hoverEvent,
		 @Nullable String insertionString,
		 @Nullable List<Mson> extra,
		 @Nullable Mson parent) {
		this(text, color == null ? null : color.name().toLowerCase(), bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertionString, extra, parent);
	}

	@Contract("_, _, _, _, _, _, _, _, _, _, _, _ -> new")
	public static @NotNull Mson valueOf(@NotNull String text, @Nullable ChatColor color, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable MsonEvent clickEvent, @Nullable MsonEvent hoverEvent, @Nullable String insertionString, @Nullable List<Mson> extra, @Nullable Mson parent) {
		return new Mson(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertionString, extra, parent);
	}

	@Contract("_, _, _, _, _, _, _, _, _, _, _, _ -> new")
	public static @NotNull Mson valueOf(@NotNull String text, @Nullable String color, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable MsonEvent clickEvent, @Nullable MsonEvent hoverEvent, @Nullable String insertionString, @Nullable List<Mson> extra, @Nullable Mson parent) {
		return new Mson(text, color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertionString, extra, parent);
	}

	// Object
	public static @NotNull Mson mson(@NotNull Object part) {
		return Mson.getMson(part);
	}

	public static @NotNull Mson mson(Object @NotNull ... parts) {
		return Mson.getMson(parts);
	}

	@Contract("null -> fail")
	private static @NotNull Mson getMson(Object part) {
		if (part == null) throw new NullPointerException("part");

		if (part instanceof Mson) {
			return (Mson) part;
		} else if (part instanceof String) {
			return mson((String) part);
		} else if (part instanceof Collection<?>) {
			Collection<?> parts = (Collection<?>) part;
			List<Mson> msons = Mson.msons(parts);

			if (msons.isEmpty()) return mson();
			if (msons.size() == 1) return msons.get(0);

			return mson().extra(msons);
		} else if (part instanceof Object[]) {
			Object[] parts = (Object[]) part;
			return getMson(Arrays.asList(parts));
		} else {
			throw new IllegalArgumentException("We only accept Strings, Msons, Collections and Arrays.");
		}
	}

	@Contract("null -> fail")
	public static @NotNull List<Mson> msons(Object... parts) {
		if (parts == null) throw new NullPointerException("parts");

		return msons(Arrays.asList(parts));
	}

	@Contract("null -> fail")
	public static @NotNull List<@NotNull Mson> msons(Iterable<?> parts) {
		if (parts == null) throw new NullPointerException("parts");

		List<Mson> msons = new KamiList<>();

		for (Object part : parts) {
			msons.add(getMson(part));
		}

		return msons;
	}

	// -------------------------------------------- //
	// PARSE & FORMAT
	// -------------------------------------------- //

	public static @NotNull Mson fromParsedMessages(@NotNull Collection<@NotNull String> messages) {
		List<Mson> extra = new KamiList<>(messages.size());
		for (String message : messages) {
			extra.add(fromParsedMessage(message));
		}
		return mson(extra);
	}

	@Contract("null -> fail")
	public static @NotNull Mson fromParsedMessage(String message) {
		return fromParsedMessage(message, true);
	}

	@Contract("null, _ -> fail")
	public static @NotNull Mson fromParsedMessage(String message, boolean allowMHex) {
		if (message == null) throw new NullPointerException("message");

		// Everything must have a color.
		// Because when we split, we assume that each part starts with a color code.
		// Here we assure it starts with one.
		message = ensureStartsWithColorCode(message);

		// We split at color/format change.
		String[] parts = PATTERN_PARSE_PREFIX.split(message);

		List<Mson> msons = new KamiList<>();

		String latestColor = null;
		Boolean bold = null;
		Boolean italic = null;
		Boolean underlined = null;
		Boolean strikethrough = null;
		Boolean obfuscated = null;

		for (String part : parts) {
			Matcher matcher = PATTERN_PARSE_PREFIX.matcher(part);

			String text;

			if (matcher.find()) {
				if (matcher.group("vhex") != null) {
					latestColor = matcher.group("vhex");
					text = part.substring(latestColor.length());

					latestColor = latestColor.replace("\u00A7x", "#");
					latestColor = latestColor.replace("\u00A7", "");
				} else if (matcher.group("code") != null) {
					ChatColor color = ChatColor.getByChar(part.charAt(1));
					text = part.substring(2);

					if ((color != null && color.isColor()) || color == ChatColor.RESET) {
						latestColor = color.name().toLowerCase();
						bold = null;
						italic = null;
						underlined = null;
						strikethrough = null;
						obfuscated = null;
					}
					if (color == ChatColor.RESET) latestColor = null;
					else if (color == ChatColor.BOLD) bold = true;
					else if (color == ChatColor.ITALIC) italic = true;
					else if (color == ChatColor.UNDERLINE) underlined = true;
					else if (color == ChatColor.STRIKETHROUGH) strikethrough = true;
					else if (color == ChatColor.MAGIC) obfuscated = true;
				} else if (allowMHex && matcher.group("mhex") != null) {
					latestColor = matcher.group("mhex");
					// The group matches just the hex, but we also need to remove the chevrons.
					text = part.substring(latestColor.length() + 2);
				} else text = part;
			} else {
				KamiCommon.get().getLogger().warning("No Match found parsing MSON");
				continue;
			}

			// Don't add empty msons.
			if (text.isEmpty()) continue;

			Mson mson = Mson.valueOf(text, latestColor, bold, italic, underlined, strikethrough, obfuscated, null, null, null, null, null);

			msons.add(mson);
		}

		return Mson.mson(msons);
	}

	@Contract(pure = true)
	private static @NotNull String ensureStartsWithColorCode(@NotNull String message) {
		if (!message.startsWith("\u00A7")) {
			message = ChatColor.RESET + message;
		}
		return message;
	}

	// Parse redirects, convert to Mson directly
	public static @NotNull Mson parse(@NotNull String string) {
		return Mson.fromParsedMessage(Txt.parse(string));
	}

	public static @NotNull Mson parse(@NotNull Collection<@NotNull String> strings) {
		return Mson.fromParsedMessages(Txt.parse(strings));
	}

	public static @NotNull Mson parse(String format, Object... args) {
		return Mson.fromParsedMessage(Txt.parse(format, args));
	}

	@Contract("_, _ -> new")
	public static @NotNull Mson format(String format, Object... args) {
		return Mson.mson(String.format(format, args));
	}

	// -------------------------------------------- //
	// STRING LIKE METHODS
	// -------------------------------------------- //

	// Case
	public @NotNull Mson toLowerCase() {
		Mson ret = this.text(this.getText().toLowerCase());

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				extra[i] = part.toLowerCase();
				i++;
			}
			ret = ret.extra(extra);
		}

		return ret;
	}

	public @NotNull Mson toUpperCase() {
		Mson ret = this.text(this.getText().toUpperCase());

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				extra[i] = part.toUpperCase();
				i++;
			}
			ret = ret.extra(extra);
		}

		return ret;
	}

	public @NotNull Mson uppercaseFirst() {
		if (!this.getText().isEmpty()) {
			return this.text(Txt.upperCaseFirst(this.getText()));
		}

		Mson ret = this;
		boolean uppercased = false;

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				if (!uppercased) {
					Mson uppercase = part.uppercaseFirst();
					uppercased = (uppercase != part);
					part = uppercase;
				}
				extra[i] = part;
				i++;
			}
			if (uppercased) {
				ret = ret.extra(extra);
			}
		}

		return ret;
	}

	// Whitespace
	public @NotNull Mson trim() {
		Mson ret = this.text(this.getText().trim());

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				extra[i] = part.trim();
				i++;
			}
			ret = ret.extra(extra);
		}

		return ret;
	}

	// Length
	public @Range(from = 0, to = Integer.MAX_VALUE) int length() {
		int ret = this.getText().length();
		if (this.hasExtra()) {
			for (Mson part : Objects.requireNonNull(this.getExtra())) {
				ret += part.length();
			}
		}
		return ret;
	}

	// Contains
	@Contract("null -> fail")
	public boolean contains(CharSequence sequence) {
		if (sequence == null) throw new NullPointerException("sequence");

		if (this.getText().contains(sequence)) return true;

		if (this.hasExtra()) {
			for (Mson part : Objects.requireNonNull(this.getExtra())) {
				if (part.contains(sequence)) return true;
			}
		}

		return false;
	}

	@Contract("null -> fail")
	public boolean contains(Pattern pattern) {
		if (pattern == null) throw new NullPointerException("pattern");

		if (pattern.matcher(this.getText()).find()) return true;

		if (this.hasExtra()) {
			for (Mson part : Objects.requireNonNull(this.getExtra())) {
				if (part.contains(pattern)) return true;
			}
		}

		return false;
	}

	@Contract("null -> fail")
	public @NotNull List<@NotNull Mson> split(String regex) {
		if (regex == null) throw new NullPointerException("regex");

		return this.split(Pattern.compile(regex));
	}

	@Contract("null -> fail")
	public @NotNull List<@NotNull Mson> split(Pattern pattern) {
		if (pattern == null) throw new NullPointerException("pattern");

		List<Mson> ret = new KamiList<>();
		Mson recent = this.splitInner(pattern, ret, null);
		if (!recent.isEmpty()) ret.add(recent);
		return ret;
	}

	private @NotNull Mson splitInner(@NotNull Pattern pattern, @NotNull List<@NotNull Mson> ret, Mson recent) {
		String[] parts = pattern.split(this.getText(), -1);

		// If it starts with a split ...
		if (parts[0].isEmpty() && !this.getText().isEmpty()) {
			// ... add the most recent.
			if (recent != null) ret.add(recent);
			else ret.add(mson());
			recent = null;
			parts = Arrays.copyOfRange(parts, 1, parts.length);
		}

		for (int i = 0; i < parts.length; i++) {
			Mson part = this.enforced().extra((List<Mson>) null).text(parts[i]);
			boolean ultimate = (i == parts.length - 1);
			if (!ultimate) {
				Mson mson;
				if (recent != null) mson = mson(recent, part);
				else mson = part;
				ret.add(mson);
				recent = null;
			} else {
				boolean empty = parts[i].isEmpty();
				if (empty && !this.getText().isEmpty()) {

					if (recent != null) ret.add(recent);
					recent = mson();
				} else {
					Mson mson;
					if (recent != null) mson = mson(recent, part);
					else mson = part;
					recent = mson;
				}
			}
		}

		if (this.hasExtra()) {
			for (Mson extra : Objects.requireNonNull(this.getExtra())) {
				recent = extra.splitInner(pattern, ret, recent);
			}
		}

		return Objects.requireNonNull(recent);
	}

	// -------------------------------------------- //
	// REPLACE
	// -------------------------------------------- //

	// Needed?
	public @NotNull Mson replace(char oldChar, char newChar) {
		Mson ret = this.text(this.getText().replace(oldChar, newChar));

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				extra[i] = part.replace(oldChar, newChar);
				i++;
			}
			ret = ret.extra(extra);
		}

		return ret;
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(String regex, String replacement) {
		if (regex == null) throw new NullPointerException("regex");
		if (replacement == null) throw new NullPointerException("replacement");

		return replaceAll(regex, mson(replacement));
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(Pattern pattern, String replacement) {
		if (pattern == null) throw new NullPointerException("pattern");
		if (replacement == null) throw new NullPointerException("replacement");

		return replaceAll(pattern, mson(replacement));
	}

	// Special replace all

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(String regex, Mson replacement) {
		if (regex == null) throw new NullPointerException("regex");
		if (replacement == null) throw new NullPointerException("replacement");
		return this.replaceAll(regex, new Mson[]{replacement});
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(Pattern pattern, final Mson replacement) {
		if (pattern == null) throw new NullPointerException("pattern");
		if (replacement == null) throw new NullPointerException("replacement");

		return this.replaceAll(pattern, new Mson[]{replacement});
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(String regex, Mson... replacements) {
		if (regex == null) throw new NullPointerException("regex");
		if (replacements == null) throw new NullPointerException("replacements");
		return this.replaceAll(Pattern.compile(regex), replacements);
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(Pattern pattern, final @NotNull Mson... replacements) {
		if (pattern == null) throw new NullPointerException("pattern");
		if (replacements == null) throw new NullPointerException("replacements");

		final AtomicInteger i = new AtomicInteger(0);
		MsonReplacement replacer = (match, parent) -> {
			int idx = i.intValue();
			i.set(idx + 1);
			return replacements[idx % replacements.length];
		};
		return this.replaceAll(pattern, replacer);
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(String regex, MsonReplacement replacer) {
		if (regex == null) throw new NullPointerException("regex");
		if (replacer == null) throw new NullPointerException("replacer");
		return this.replaceAll(Pattern.compile(regex), replacer);
	}

	public @NotNull Mson replaceAll(Pattern pattern, MsonReplacement replacer) {
		if (pattern == null) throw new NullPointerException("pattern");
		if (replacer == null) throw new NullPointerException("replacer");

		Mson ret = this.text("");

		List<Mson> msons = new ArrayList<>();
		StringBuffer currentString = new StringBuffer();
		Matcher matcher = pattern.matcher(this.getText());
		while (matcher.find()) {
			String match = matcher.group(0);
			Mson replacement = replacer.getReplacement(match, this);

			// Add the match
			if (replacement == null) matcher.appendReplacement(currentString, match);

				// Add the string
			else if (replacement.isTextOnly()) matcher.appendReplacement(currentString, replacement.getText());

				// Add the mson
			else {
				// Fixup current string
				matcher.appendReplacement(currentString, "");
				if (addStringBuffer(msons, currentString)) currentString = new StringBuffer();

				// Add this replacement
				msons.add(replacement);
			}
		}

		// Add the remaining string pieces
		matcher.appendTail(currentString);
		addStringBuffer(msons, currentString);

		// Recurse on extras.
		if (this.hasExtra()) {
			for (Mson extra : Objects.requireNonNull(this.getExtra())) {
				msons.add(extra.replaceAll(pattern, replacer));
			}
		}

		// Set extras
		ret = ret.extra(msons);

		return ret;
	}

	private static boolean addStringBuffer(@NotNull List<Mson> msons, @NotNull StringBuffer buffer) {
		if (buffer.length() == 0) return false;
		Mson mson = mson(buffer.toString());
		msons.add(mson);
		return true;
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull Mson replaceAll(Mson replace, Mson replacement) {
		if (replace == null) throw new NullPointerException("replace");
		if (replacement == null) throw new NullPointerException("replacement");

		Mson ret = this;

		if (this.hasExtra()) {
			Mson[] extra = new Mson[Objects.requireNonNull(this.getExtra()).size()];
			int i = 0;
			for (Mson part : this.getExtra()) {
				extra[i] = part.equals(replace) ? replacement : part;
				i++;
			}
			ret = ret.extra(extra);
		}

		return ret;
	}

	// -------------------------------------------- //
	// IMPLODE
	// -------------------------------------------- //

	// Implode simple
	public static @NotNull Mson implode(final @Nullable Object @NotNull [] list, final Mson glue, final @Nullable Mson format) {
		List<Mson> parts = new KamiList<>();
		for (int i = 0; i < list.length; i++) {
			Object item = list[i];
			Mson part = (item == null ? NULL : Mson.mson(item));

			if (i != 0) {
				parts.add(glue);
			}
			if (format != null) {
				part = format.replaceAll("%s", part);
			}
			parts.add(part);
		}

		return Mson.mson(parts);
	}

	public static @NotNull Mson implode(final @Nullable Object @NotNull [] list, final Mson glue) {
		return implode(list, glue, null);
	}

	public static @NotNull Mson implode(final @NotNull Collection<?> coll, final Mson glue, final @Nullable Mson format) {
		return implode(coll.toArray(new Object[0]), glue, format);
	}

	public static @NotNull Mson implode(final @NotNull Collection<?> coll, final Mson glue) {
		return implode(coll, glue, null);
	}

	// Implode comma and dot
	public static Mson implodeCommaAndDot(@NotNull Collection<?> objects, Mson format, Mson comma, Mson and, Mson dot) {
		if (objects.isEmpty()) return mson();
		if (objects.size() == 1) {
			return implode(objects, comma, format);
		}

		List<Object> ourObjects = new KamiList<>(objects);

		Mson ultimateItem = mson(ourObjects.remove(ourObjects.size() - 1));
		Mson penultimateItem = mson(ourObjects.remove(ourObjects.size() - 1));
		if (format != null) {
			ultimateItem = format.replaceAll("%s", ultimateItem);
			penultimateItem = format.replaceAll("%s", penultimateItem);
		}
		Mson merge = mson(penultimateItem, and, ultimateItem);
		ourObjects.add(merge);

		return implode(ourObjects, comma, format).add(mson(dot));
	}

	public static Mson implodeCommaAndDot(final @NotNull Collection<?> objects, Mson comma, Mson and, Mson dot) {
		return implodeCommaAndDot(objects, null, comma, and, dot);
	}

	public static Mson implodeCommaAnd(final @NotNull Collection<?> objects, Mson comma, Mson and) {
		return implodeCommaAndDot(objects, comma, and, mson());
	}

	public static Mson implodeCommaAndDot(final @NotNull Collection<?> objects, ChatColor color) {
		return implodeCommaAndDot(objects, COMMA_SPACE.color(color), SPACE_AND_SPACE.color(color), DOT.color(color));
	}

	public static Mson implodeCommaAnd(final @NotNull Collection<?> objects, ChatColor color) {
		return implodeCommaAndDot(objects, COMMA_SPACE.color(color), SPACE_AND_SPACE.color(color), mson());
	}

	public static Mson implodeCommaAndDot(final @NotNull Collection<?> objects, String color) {
		return implodeCommaAndDot(objects, COMMA_SPACE.color(color), SPACE_AND_SPACE.color(color), DOT.color(color));
	}

	public static Mson implodeCommaAnd(final @NotNull Collection<?> objects, String color) {
		return implodeCommaAndDot(objects, COMMA_SPACE.color(color), SPACE_AND_SPACE.color(color), mson());
	}

	public static Mson implodeCommaAndDot(final @NotNull Collection<?> objects) {
		return implodeCommaAndDot(objects, (String) null);
	}

	public static Mson implodeCommaAnd(final @NotNull Collection<?> objects) {
		return implodeCommaAnd(objects, (String) null);
	}

	// -------------------------------------------- //
	// PREPONDFIX
	// -------------------------------------------- //
	// This weird algorithm takes:
	// - A prefix
	// - A centerpiece single string or a list of strings.
	// - A suffix
	// If the centerpiece is a single String it just concatenates prefix + centerpiece + suffix.
	// If the centerpiece is multiple Strings it concatenates prefix + suffix and then appends the centerpice at the end.
	// This algorithm is used in the editor system.

	public static @NotNull List<Mson> prepondfix(@Nullable Mson prefix, @NotNull List<Mson> msons, @Nullable Mson suffix) {
		// Create
		List<Mson> ret = new KamiList<>();

		// Fill
		List<Mson> parts = new KamiList<>();
		if (prefix != null) parts.add(prefix);
		if (msons.size() == 1) parts.add(msons.get(0));
		if (suffix != null) parts.add(suffix);

		if (!parts.isEmpty()) {
			ret.add(implode(parts, SPACE));
		}

		if (msons.size() != 1) {
			ret.addAll(msons);
		}

		// Return
		return ret;
	}

	public static @NotNull Mson prepondfix(@Nullable Mson prefix, @NotNull Mson mson, @Nullable Mson suffix) {
		List<Mson> msons = mson.split(Txt.PATTERN_NEWLINE);
		List<Mson> ret = prepondfix(prefix, msons, suffix);
		return implode(ret, NEWLINE);
	}


	// -------------------------------------------- //
	// MESSAGE
	// -------------------------------------------- //

	// All
	public boolean messageAll() {
		return MsonMessenger.get().messageAll(this);
	}

	// Predicate
	public boolean messagePredicate(Predicate<CommandSender> predicate) {
		return MsonMessenger.get().messagePredicate(predicate, this);
	}

	// One
	public boolean messageOne(Object senderObject) {
		return MsonMessenger.get().messageOne(senderObject, this);
	}

	// -------------------------------------------- //
	// TO JSON, RAW, PLAIN & STRING
	// -------------------------------------------- //

	public JsonElement toJson() {
		return toJson(this);
	}

	public static JsonElement toJson(@Nullable Mson mson) {
		if (mson == null) return JsonNull.INSTANCE;
		return getGson(true).toJsonTree(mson);
	}

	public static @Nullable Mson fromJson(@NotNull JsonElement json) {
		// Escape the null.
		if (json.isJsonNull()) {
			return null;
		}

		// If converting from an old system.
		if (json.isJsonPrimitive() && ((JsonPrimitive) json).isString()) {
			return fromParsedMessage(json.getAsString());
		}

		// Just a normal mson.
		if (json.isJsonObject()) {
			Mson ret = getGson(true).fromJson(json, Mson.class);

			MsonEvent event;

			event = ret.getEvent(MsonEventType.CLICK);
			if (event != null) event.repair();

			event = ret.getEvent(MsonEventType.HOVER);
			if (event != null) event.repair();

			return ret;
		}

		// Something is horribly wrong.
		throw new IllegalArgumentException("Neither string nor object: " + json);
	}

	private transient String raw = null;

	public String toRaw() {
		if (raw == null) raw = this.toJson().toString();
		return raw;
	}

	public static @NotNull List<@NotNull String> toPlain(@NotNull Iterable<@NotNull Mson> iterable, boolean styled) {
		List<String> ret = new KamiList<>();

		for (Mson mson : iterable) {
			ret.add(mson.toPlain(styled));
		}

		return ret;
	}

	public @NotNull String toPlain(boolean styled) {
		final StringBuilder ret = new StringBuilder();
		this.toPlain0(ret, styled);
		return ret.toString();
	}

	private void toPlain0(final @NotNull StringBuilder builder, boolean styled) {
		if (!this.getText().isEmpty()) {
			// Color must be put in BEFORE formatting.
			// http://minecraft.gamepedia.com/Formatting_codes#Formatting_codes
			if (styled) {
				if (this.getEffectiveColorCode() != null) builder.append(this.getEffectiveColorCode());
				if (this.isEffectiveBold() != null && Boolean.TRUE.equals(this.isEffectiveBold())) builder.append(ChatColor.BOLD);
				if (this.isEffectiveItalic() != null && Boolean.TRUE.equals(this.isEffectiveItalic())) builder.append(ChatColor.ITALIC);
				if (this.isEffectiveUnderlined() != null && Boolean.TRUE.equals(this.isEffectiveUnderlined()))
					builder.append(ChatColor.UNDERLINE);
				if (this.isEffectiveStrikethrough() != null && Boolean.TRUE.equals(this.isEffectiveStrikethrough()))
					builder.append(ChatColor.STRIKETHROUGH);
				if (this.isEffectiveObfuscated() != null && Boolean.TRUE.equals(this.isEffectiveObfuscated()))
					builder.append(ChatColor.MAGIC);
			}

			builder.append(this.getText());
		}

		if (this.hasExtra()) {
			for (Mson part : Objects.requireNonNull(this.getExtra())) {
				if (styled) {
					builder.append(ChatColor.RESET);
				}

				part.toPlain0(builder, styled);
			}
		}
	}


	@Override
	public String toString() {
		return this.toRaw();
	}

	// -------------------------------------------- //
	// EQUALS AND HASHCODE
	// -------------------------------------------- //

	@Override
	public int hashCode() {
		return Objects.hash(
				this.text,
				this.color,
				this.bold,
				this.italic,
				this.underlined,
				this.strikethrough,
				this.obfuscated,
				this.clickEvent,
				this.hoverEvent,
				this.insertion,
				this.extra
		);
	}

	@Contract(value = "null -> false", pure = true)
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof Mson)) return false;
		Mson that = (Mson) object;
		return KUtil.equals(
				this.text, that.text,
				this.color, that.color,
				this.bold, that.bold,
				this.italic, that.italic,
				this.underlined, that.underlined,
				this.strikethrough, that.strikethrough,
				this.obfuscated, that.obfuscated,
				this.clickEvent, that.clickEvent,
				this.hoverEvent, that.hoverEvent,
				this.insertion, that.insertion,
				this.extra, that.extra
		);
	}

}

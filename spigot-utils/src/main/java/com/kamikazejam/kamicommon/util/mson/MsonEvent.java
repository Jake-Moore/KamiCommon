package com.kamikazejam.kamicommon.util.mson;

import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("unused")
@Getter
public final class MsonEvent implements Serializable {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	@Setter
	private MsonEventAction action;

	private final String value;

	// -------------------------------------------- //
	// REPAIR
	// -------------------------------------------- //
	public void repair() {
		MsonEventAction action = this.getAction();
		if (action != null) return;

		String value = this.getValue();
		if (value == null) return;

		action = guessAction(value);
		this.setAction(action);
	}

	private static @NotNull MsonEventAction guessAction(@NotNull String value) {
		if (value.startsWith("{id:")) return MsonEventAction.SHOW_ITEM;
		if (value.startsWith("/")) return MsonEventAction.SUGGEST_COMMAND;
		if (value.startsWith("http")) return MsonEventAction.OPEN_URL;
		return MsonEventAction.SHOW_TEXT;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	private MsonEvent(MsonEventAction action, String value) {
		this.action = action;
		this.value = value;
	}

	// NoArg Constructor for GSON 
	private MsonEvent() {
		this(null, null);
	}

	// -------------------------------------------- //
	// TOOLTIP
	// -------------------------------------------- //

	public @Nullable String createTooltip() {
		String prefix = this.getAction().getTooltipPrefix();
		if (prefix == null) return null;
		return prefix + this.getValue();
	}

	// -------------------------------------------- //
	// FACTORY
	// -------------------------------------------- //

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull MsonEvent valueOf(MsonEventAction action, String value) {
		return new MsonEvent(action, value);
	}

	// clickEvents
	@Contract(value = "_ -> new", pure = true)
	public static @NotNull MsonEvent link(String url) {
		return MsonEvent.valueOf(MsonEventAction.OPEN_URL, url);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull MsonEvent suggest(String replace) {
		return MsonEvent.valueOf(MsonEventAction.SUGGEST_COMMAND, replace);
	}

	@Contract("_, _ -> new")
	public static @NotNull MsonEvent suggest(@NotNull KamiCommand cmd, String... args) {
		return suggest(cmd.getCommandLine(args));
	}

	@Contract("_, _ -> new")
	public static @NotNull MsonEvent suggest(@NotNull KamiCommand cmd, Iterable<String> args) {
		return suggest(cmd.getCommandLine(args));
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull MsonEvent command(String cmd) {
		return MsonEvent.valueOf(MsonEventAction.RUN_COMMAND, cmd);
	}

	@Contract("_, _ -> new")
	public static @NotNull MsonEvent command(@NotNull KamiCommand cmd, String... args) {
		return command(cmd.getCommandLine(args));
	}

	@Contract("_, _ -> new")
	public static @NotNull MsonEvent command(@NotNull KamiCommand cmd, Iterable<String> args) {
		return command(cmd.getCommandLine(args));
	}

	// showText
	@Contract(value = "_ -> new", pure = true)
	public static @NotNull MsonEvent tooltip(String hoverText) {
		return MsonEvent.valueOf(MsonEventAction.SHOW_TEXT, hoverText);
	}

	@Contract("_ -> new")
	public static @NotNull MsonEvent tooltip(String @NotNull ... hoverTexts) {
		return MsonEvent.valueOf(MsonEventAction.SHOW_TEXT, Txt.implode(hoverTexts, "\n"));
	}

	@Contract("_ -> new")
	public static @NotNull MsonEvent tooltip(@NotNull Collection<String> hoverTexts) {
		return MsonEvent.valueOf(MsonEventAction.SHOW_TEXT, Txt.implode(hoverTexts, "\n"));
	}

	// showTextParsed
	@Contract("_ -> new")
	public static @NotNull MsonEvent tooltipParse(String hoverText) {
		return MsonEvent.valueOf(MsonEventAction.SHOW_TEXT, Txt.parse(hoverText));
	}

	@Contract("_ -> new")
	public static @NotNull MsonEvent tooltipParse(String @NotNull ... hoverTexts) {
		return MsonEvent.valueOf(MsonEventAction.SHOW_TEXT, Txt.parse(Txt.implode(hoverTexts, "\n")));
	}

	@Contract("_ -> new")
	public static @NotNull MsonEvent tooltipParse(@NotNull Collection<String> hoverTexts) {
		return MsonEvent.valueOf(MsonEventAction.SHOW_TEXT, Txt.parse(Txt.implode(hoverTexts, "\n")));
	}

	// showItem
	@Contract("null -> fail")
	public static @NotNull MsonEvent item(ItemStack item) {
		if (item == null) throw new NullPointerException("item");
		item = getItemSanitizedForTooltip(item);

		String value = NmsAPI.getItemText().getNbtStringTooltip(item);
		return MsonEvent.valueOf(MsonEventAction.SHOW_ITEM, value);
	}

	@Contract("null -> fail")
	private static @NotNull ItemStack getItemSanitizedForTooltip(ItemStack item) {
		if (item == null) throw new NullPointerException("item");

		if (!item.hasItemMeta()) return item;

		ItemMeta meta = item.getItemMeta();

		if (meta instanceof BookMeta) {
			BookMeta book = (BookMeta) meta;
			book.setPages();
			item = item.clone();
			item.setItemMeta(meta);
			return item;
		}

		return item;
	}

	// -------------------------------------------- //
	// CONVENIENCE
	// -------------------------------------------- //

	public @NotNull MsonEventType getType() {
		return this.getAction().getType();
	}

	// -------------------------------------------- //
	// JSON
	// -------------------------------------------- //

	public static MsonEvent fromJson(JsonElement json) {
		return Mson.getGson(true).fromJson(json, MsonEvent.class);
	}

	public static JsonElement toJson(MsonEvent event) {
		return Mson.getGson(true).toJsonTree(event);
	}

	// -------------------------------------------- //
	// EQUALS AND HASHCODE
	// -------------------------------------------- //

	@Override
	public int hashCode() {
		return Objects.hash(
				this.action,
				this.value
		);
	}

	@Contract(value = "null -> false", pure = true)
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof MsonEvent)) return false;
		MsonEvent that = (MsonEvent) object;

		return KUtil.equals(
				this.action, that.action,
				this.value, that.value
		);
	}

}

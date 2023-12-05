package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.mson.Mson;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.kamikazejam.kamicommon.util.mson.Mson.mson;

@SuppressWarnings("unused")
public class Parameter<T> {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final String DEFAULT_DESC_DEFAULT = null;
	public static final Object DEFAULT_VALUE_DEFAULT = null;
	public static final boolean REQUIRED_FROM_CONSOLE_DEFAULT = false;
	public static final String DESCRIPTION_DEFAULT = null;

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	@Getter
	protected Type<T> type;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setType(Type<T> type) {
		this.type = type;
		return this;
	}

	@Getter
	protected String name;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setName(String name) {
		this.name = name;
		return this;
	}

	protected String defaultDesc = null;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setDefaultDesc(String defaultDesc) {
		this.defaultDesc = defaultDesc;
		return this;
	}

	public String getDefaultDesc() {
		if (this.defaultDesc != null) return defaultDesc;
		if (this.isDefaultValueSet()) return String.valueOf(this.getDefaultValue());
		return null;
	}

	@Getter
	protected T defaultValue = null;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		this.defaultValueSet = true;
		return this;
	}

	// A default value can be null.
	// So we must keep track of this field too.
	@Getter
	protected boolean defaultValueSet = false;

	@Contract(mutates = "this")
	public void setDefaultValueSet(boolean defaultValueSet) {
		this.defaultValueSet = defaultValueSet;
	}


	// Convenience
	public boolean isRequired() {
		return this.getDefaultDesc() == null;
	}

	public boolean isOptional() {
		return !this.isRequired();
	}

	// Is this arg ALWAYS required from the console?
	// That might the case if the arg is a player. and default is oneself.
	@Getter
	protected boolean requiredFromConsole = false;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setRequiredFromConsole(boolean requiredFromConsole) {
		this.requiredFromConsole = requiredFromConsole;
		return this;
	}

	// An optional description of this argument.
	// Examples:
	// 1. "the faction to show info about"
	// 2. "the ticket to pick"
	// 3. "the amount of money to pay"
	@Getter
	protected String desc = null;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	public boolean hasDesc() {
		return this.getDesc() != null;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	// To minimize confusion and mixing of arguments for the constructor
	// description must not be set in the constructor.

	// All
	@Contract("_, null, _, _, _ -> fail; _, !null, _, null, _ -> fail")
	public Parameter(T defaultValue, Type<T> type, boolean requiredFromConsole, String name, String defaultDesc) {
		// Null checks
		if (type == null) throw new IllegalArgumentException("type mustn't be null");
		if (name == null) throw new IllegalArgumentException("name mustn't be null");

		this.setType(type);
		this.setRequiredFromConsole(requiredFromConsole);
		this.setName(name);
		this.setDefaultDesc(defaultDesc);
		this.setDefaultValue(defaultValue);
	}

	// Without defaultValue
	@SuppressWarnings("unchecked")
	public Parameter(@NotNull Type<T> type, boolean requiredFromConsole, @NotNull String name, String defaultDesc) {
		this((T) DEFAULT_VALUE_DEFAULT, type, requiredFromConsole, name, defaultDesc);

		// In fact the default value is not set.
		this.defaultValueSet = false;
	}

	// Without reqFromConsole.
	public Parameter(T defaultValue, @NotNull Type<T> type, @NotNull String name, String defaultDesc) {
		this(defaultValue, type, REQUIRED_FROM_CONSOLE_DEFAULT, name, defaultDesc);
	}

	// Without defaultDesc.
	public Parameter(T defaultValue, @NotNull Type<T> type, boolean requiredFromConsole, @NotNull String name) {
		this(defaultValue, type, requiredFromConsole, name, DEFAULT_DESC_DEFAULT);
	}

	// Without defaultValue & reqFromConsole.
	public Parameter(@NotNull Type<T> type, @NotNull String name, String defaultDesc) {
		this(type, REQUIRED_FROM_CONSOLE_DEFAULT, name, defaultDesc);
	}

	// Without defaultValue & defaultDesc.
	public Parameter(@NotNull Type<T> type, boolean requiredFromConsole, @NotNull String name) {
		this(type, requiredFromConsole, name, DEFAULT_DESC_DEFAULT);
	}

	// Without reqFromConsole and defaultDesc.
	public Parameter(T defaultValue, @NotNull Type<T> type, @NotNull String name) {
		this(defaultValue, type, REQUIRED_FROM_CONSOLE_DEFAULT, name, DEFAULT_DESC_DEFAULT);
	}

	// Without defaultValue, reqFromConsole and defaultDesc.
	public Parameter(@NotNull Type<T> type, @NotNull String name) {
		this(type, REQUIRED_FROM_CONSOLE_DEFAULT, name, DEFAULT_DESC_DEFAULT);
	}

	// Without defaultValue, name, reqFromConsole and defaultDesc.
	public Parameter(@NotNull Type<T> type) {
		this(type, REQUIRED_FROM_CONSOLE_DEFAULT, type.getName(), DEFAULT_DESC_DEFAULT);
	}

	// -------------------------------------------- //
	// CONVENIENCE
	// -------------------------------------------- //

	public boolean isRequiredFor(CommandSender sender) {
		if (this.isRequired()) return true; // Required for everyone.
		if (!this.isRequiredFromConsole()) return false; // If not required for console. Then not anyone.
		if (sender == null) return false; // If null we will suppose it is a player.
		return !(sender instanceof Player); // Required for console.
// Not required.
	}

	public boolean isOptionalFor(CommandSender sender) {
		return !this.isRequiredFor(sender);
	}

	public Mson getTemplate(CommandSender sender) {
		Mson ret;

		if (this.isRequiredFor(sender)) {
			ret = mson("<" + this.getName() + ">");
		} else {
			String def = this.getDefaultDesc();
			def = (def != null ? "=" + def : "");
			ret = mson("[" + this.getName() + def + "]");
		}

		if (this.hasDesc()) ret = ret.tooltip(Txt.upperCaseFirst(this.getDesc()));

		return ret;
	}

	// -------------------------------------------- //
	// COMMONLY USED PARAMETERS
	// -------------------------------------------- //

	@Contract(" -> new")
	public static Parameter<Integer> getPage() {
		// We can't use a singleton because people might want to set a description.
		return new Parameter<>(1, TypeInteger.get(), "page", "1").setDesc("page");
	}

}

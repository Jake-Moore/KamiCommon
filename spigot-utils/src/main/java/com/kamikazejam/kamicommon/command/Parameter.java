package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
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

	protected Type<T> type;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setType(Type<T> type) {
		this.type = type;
		return this;
	}

	protected String name;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setName(String name) {
		this.name = name;
		return this;
	}

	protected T defaultValue = null;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		this.defaultValueSet = true;
		return this;
	}

	// A default value can be null.
	// So we must keep track of this field too.
	protected boolean defaultValueSet = false;

	@Contract(mutates = "this")
	public void setDefaultValueSet(boolean defaultValueSet) {
		this.defaultValueSet = defaultValueSet;
	}


	// Convenience
	public boolean isRequired() {
		return this.getDefaultValue() == null;
	}

	public boolean isOptional() {
		return !this.isRequired();
	}

	// Is this arg ALWAYS required from the console?
	// That might the case if the arg is a player. and default is oneself.
	protected boolean requiredFromConsole = false;

	@Contract(value = "_ -> this", mutates = "this")
	public Parameter<T> setRequiredFromConsole(boolean requiredFromConsole) {
		this.requiredFromConsole = requiredFromConsole;
		return this;
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

	public boolean isRequiredFor(@Nullable CommandSender sender) {
		if (this.isRequired()) return true; // Required for everyone.
		if (!this.isRequiredFromConsole()) return false; // If not required for console. Then not anyone.
		if (sender == null) return false; // If null we will suppose it is a player.
		return !(sender instanceof Player); // Required for console.
		// Not required.
	}

	public boolean isOptionalFor(CommandSender sender) {
		return !this.isRequiredFor(sender);
	}

	@NotNull
	public String getTemplate(@Nullable CommandSender sender) {
		String ret;

		if (this.isRequiredFor(sender)) {
			ret = "<" + this.getName() + ">";
		} else {
			@Nullable String defVal = (this.isDefaultValueSet() ? String.valueOf(this.getDefaultValue()) : null);
			ret = "[" + this.getName() + (defVal != null ? "=" + defVal : "") + "]";
		}
		return ret;
	}

	// -------------------------------------------- //
	// COMMONLY USED PARAMETERS
	// -------------------------------------------- //

	@Contract(" -> new")
	public static Parameter<Integer> getPage() {
		// We can't use a singleton because people might want to set a description.
		return new Parameter<>(1, TypeInteger.get(), "page", "1");
	}

}

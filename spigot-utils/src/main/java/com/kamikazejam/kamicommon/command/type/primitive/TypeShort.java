package com.kamikazejam.kamicommon.command.type.primitive;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TypeShort extends TypeAbstractNumber<Short> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeShort i = new TypeShort();

	public static TypeShort get() {
		return i;
	}

	public TypeShort() {
		super(Short.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public @NotNull String getName() {
		return "number";
	}

	@Override
	public Short valueOf(String arg, CommandSender sender) throws Exception {
		return Short.parseShort(arg);
	}

}

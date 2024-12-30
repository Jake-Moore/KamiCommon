package com.kamikazejam.kamicommon.command.type.primitive;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TypeFloat extends TypeAbstractNumber<Float> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeFloat i = new TypeFloat();

	public static TypeFloat get() {
		return i;
	}

	public TypeFloat() {
		super(Float.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public @NotNull String getName() {
		return "number with decimals";
	}

	@Override
	public Float valueOf(String arg, CommandSender sender) throws Exception {
		float ret = Float.parseFloat(arg);
		if (!Float.isFinite(ret)) throw new Exception();
		return ret;
	}

}

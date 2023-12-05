package com.kamikazejam.kamicommon.command.type.primitive;

import org.bukkit.command.CommandSender;

public class TypeLong extends TypeAbstractNumber<Long> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeLong i = new TypeLong();

	public static TypeLong get() {
		return i;
	}

	public TypeLong() {
		super(Long.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getName() {
		return "number";
	}

	@Override
	public Long valueOf(String arg, CommandSender sender) throws Exception {
		return Long.parseLong(arg);
	}

}

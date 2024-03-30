package com.kamikazejam.kamicommon.command.type.primitive;

import org.bukkit.command.CommandSender;

public class TypeByte extends TypeAbstractNumber<Byte> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeByte i = new TypeByte();

	public static TypeByte get() {
		return i;
	}

	public TypeByte() {
		super(Byte.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getName() {
		return "small number";
	}

	@Override
	public Byte valueOf(String arg, CommandSender sender) throws Exception {
		return Byte.parseByte(arg);
	}

}

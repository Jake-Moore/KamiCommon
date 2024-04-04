package com.kamikazejam.kamicommon.command.type.primitive;

import org.bukkit.command.CommandSender;

public class TypeDouble extends TypeAbstractNumber<Double> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeDouble i = new TypeDouble();

	public static TypeDouble get() {
		return i;
	}

	public TypeDouble() {
		super(Double.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getName() {
		return "number with decimals";
	}

	@Override
	public Double valueOf(String arg, CommandSender sender) throws Exception {
		double ret = Double.parseDouble(arg);
		if (!Double.isFinite(ret)) throw new Exception();
		return ret;
	}

}

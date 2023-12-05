package com.kamikazejam.kamicommon.command.type.primitive;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.Txt;
import org.bukkit.command.CommandSender;

public class TypeStringParsed extends TypeString {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeStringParsed i = new TypeStringParsed();

	public static TypeStringParsed get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getName() {
		return "colored text";
	}

	@Override
	public String read(String arg, CommandSender sender) throws KamiCommonException {
		return Txt.parse(super.read(arg, sender));
	}

}

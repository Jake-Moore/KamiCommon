package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.Txt;
import org.bukkit.command.CommandSender;

public abstract class TypeAbstractSimple<T> extends TypeAbstractException<T> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public TypeAbstractSimple(Class<? extends T> clazz) {
		super(clazz);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String extractErrorMessage(String arg, CommandSender sender, Exception ex) {
		return Txt.parse("<b>\"<h>%s<b>\" is not a %s.", arg, this.getName());
	}

}

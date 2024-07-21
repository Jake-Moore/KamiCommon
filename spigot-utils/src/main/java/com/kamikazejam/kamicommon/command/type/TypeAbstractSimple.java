package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.StringUtil;
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
        return StringUtil.t(String.format("&c\"&d%s&c\" is not a %s.", arg, this.getName()));
    }

}

package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.StringUtil;
import org.bukkit.ChatColor;
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
        ChatColor error = KamiCommand.Lang.getErrorColor();
        ChatColor param = KamiCommand.Lang.getErrorParamColor();
        return StringUtil.t(String.format(error + "\"" + param + "%s" + error + "\" is not a %s.", arg, this.getName()));
    }

}

package com.kamikazejam.kamicommon.command.type.primitive;

import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Collections;

public class TypeString extends TypeAbstract<String> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeString i = new TypeString();

	public static TypeString get() {
		return i;
	}

	public TypeString() {
		super(String.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getName() {
		return "text";
	}

	@Override
	public String read(String arg, CommandSender sender) throws KamiCommonException {
		return arg;
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return Collections.emptySet();
	}

}

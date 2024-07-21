package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

// Not using NamespacedKey because it's deprecated in newer versions
public class TypeNamespacedKey extends TypeAbstract<String> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeNamespacedKey i = new TypeNamespacedKey();

	public static TypeNamespacedKey get() {
		return i;
	}

	public TypeNamespacedKey() {
		super(String.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String read(String arg, CommandSender sender) throws KamiCommonException {
		return validate(arg);
	}

	public @Nullable String validate(String x) {
		if (x == null) { return null; }

		String[] parts = x.toLowerCase().split(":");
		if (parts.length == 1) { return "minecraft" + ":" + parts[0]; }

		return parts[0] + ":" + parts[1];
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return Collections.emptySet();
	}
}

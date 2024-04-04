package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.Txt;
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
	public String getVisualInner(String value, CommandSender sender) {
		String namespaceColour = "<blue>";

		String[] parts = value.split(":");
		String namespace = parts[0];
		String key = parts[1];

		if (namespace.equals("minecraft")) namespaceColour = "<green>";
		else if (namespace.equals("bukkit")) namespaceColour = "<purple>";

		return Txt.parse(namespaceColour + "%s<n>:<v>%s", namespace, key);
	}

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

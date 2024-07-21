package com.kamikazejam.kamicommon.command.type;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class TypeUUID extends TypeAbstractSimple<UUID> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeUUID i = new TypeUUID();

	public static TypeUUID get() {
		return i;
	}

	public TypeUUID() {
		super(UUID.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getName() {
		return "UUID";
	}

	@Override
	public @Nullable String getId(@Nullable UUID value) {
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public UUID valueOf(String arg, CommandSender sender) throws Exception {
		if (arg.equalsIgnoreCase("random")) return UUID.randomUUID();
		return UUID.fromString(arg);
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return Collections.emptySet();
	}

}

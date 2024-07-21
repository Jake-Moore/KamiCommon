package com.kamikazejam.kamicommon.command.type;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TypeWorld extends TypeAbstractChoice<World> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeWorld i = new TypeWorld();

	public static TypeWorld get() {
		return i;
	}

	public TypeWorld() {
		super(World.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public @Nullable String getId(@Nullable World value) {
		if (value == null) return null;
		return value.getName();
	}

	@Override
	public Collection<World> getAll() {
		return Bukkit.getWorlds();
	}

}

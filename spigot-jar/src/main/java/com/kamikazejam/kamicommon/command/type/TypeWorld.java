package com.kamikazejam.kamicommon.command.type;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

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
	public String getVisualInner(World value, CommandSender sender) {
		return value.getName();
	}

	@Override
	public String getNameInner(World value) {
		return value.getName();
	}

	@Override
	public String getIdInner(World value) {
		return value.getName();
	}

	@Override
	public Collection<World> getAll() {
		return Bukkit.getWorlds();
	}

}

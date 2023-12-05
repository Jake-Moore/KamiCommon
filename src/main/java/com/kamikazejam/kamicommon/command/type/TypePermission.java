package com.kamikazejam.kamicommon.command.type;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.util.Collection;

public class TypePermission extends TypeAbstractChoice<Permission> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypePermission i = new TypePermission();

	public static TypePermission get() {
		return i;
	}

	public TypePermission() {
		super(Permission.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getIdInner(Permission value) {
		return value.getName();
	}

	@Override
	public Collection<Permission> getAll() {
		return Bukkit.getPluginManager().getPermissions();
	}

	@Override
	public Permission getExactMatch(String arg) {
		return Bukkit.getPluginManager().getPermission(arg);
	}

}

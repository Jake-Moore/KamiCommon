package com.kamikazejam.kamicommon.command.type.enumeration;

import org.bukkit.Material;

@SuppressWarnings("unused")
public class TypeMaterial extends TypeEnumChoice<Material> {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeMaterial i = new TypeMaterial();
	public static TypeMaterial get() { return i; }
	public static TypeMaterial get(Material... exclusions) { return new TypeMaterial(exclusions); }
	private TypeMaterial() {
		super(Material.class);
	}
	private TypeMaterial(Material... exclusions) {
		super(Material.class, exclusions);
	}
}

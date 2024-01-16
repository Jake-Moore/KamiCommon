package com.kamikazejam.kamicommon.command.type.enumeration;

import org.bukkit.Material;

@SuppressWarnings("unused")
public class TypeMaterial extends TypeEnumChoice<Material> {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeMaterial i = new TypeMaterial();
	public static TypeMaterial get() { return i; }
	private TypeMaterial() {
		super(Material.class);
	}
}

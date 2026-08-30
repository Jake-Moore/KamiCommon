package com.kamikazejam.kamicommon.command.type.enumeration.xseries;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.command.type.enumeration.TypeEnumChoice;

/**
 * An {@link XMaterial} matched by constant name, giving one identifier that resolves across server versions.
 * {@link #get(XMaterial...)} builds a variant that rejects the given constants, in place of the shared
 * instance.
 */
@SuppressWarnings("unused")
public class TypeXMaterial extends TypeEnumChoice<XMaterial> {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeXMaterial i = new TypeXMaterial();
	public static TypeXMaterial get() { return i; }
	public static TypeXMaterial get(XMaterial... exclusions) { return new TypeXMaterial(exclusions); }
	private TypeXMaterial() {
		super(XMaterial.class);
	}
	private TypeXMaterial(XMaterial... exclusions) {
		super(XMaterial.class, exclusions);
	}
}

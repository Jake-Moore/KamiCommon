package com.kamikazejam.kamicommon.command.type.primitive;

public class TypeBooleanYes extends TypeBooleanAbstract {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeBooleanYes i = new TypeBooleanYes();

	public static TypeBooleanYes get() {
		return i;
	}

	public TypeBooleanYes() {
		super(NAME_YES, NAME_NO);
	}

}

package com.kamikazejam.kamicommon.command.type.enumeration;

import org.bukkit.GameMode;

@SuppressWarnings("unused")
public class TypeGameMode extends TypeEnumChoice<GameMode> {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeGameMode i = new TypeGameMode();
	public static TypeGameMode get() { return i; }
	private TypeGameMode() {
		super(GameMode.class);
	}
}

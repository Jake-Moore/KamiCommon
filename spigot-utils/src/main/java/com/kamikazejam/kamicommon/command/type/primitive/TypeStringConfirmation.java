package com.kamikazejam.kamicommon.command.type.primitive;

import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * The raw argument passed through unchanged, as {@link TypeString} does, but named "confirmation text" so
 * that a usage template reads as a prompt to retype something.
 */
public class TypeStringConfirmation extends TypeAbstract<String> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeStringConfirmation i = new TypeStringConfirmation();

	public static TypeStringConfirmation get() {
		return i;
	}

	public TypeStringConfirmation() {
		super(String.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public @NotNull String getName() {
		return "confirmation text";
	}

	@Override
	public String read(String arg, CommandSender sender) {
		return arg;
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return Collections.emptySet();
	}

}

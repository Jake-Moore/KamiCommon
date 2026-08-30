package com.kamikazejam.kamicommon.command.type.primitive;

import com.kamikazejam.kamicommon.command.type.TypeAbstractSimple;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The base for the numeric types, each parsing through its own wrapper's {@code parse} method so that a
 * non-numeric or out-of-range argument fails with the standard "is not a number" message. Tab completion
 * offers a single {@code "1"} rather than enumerating values.
 */
public abstract class TypeAbstractNumber<T extends Number> extends TypeAbstractSimple<T> {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final List<String> TAB_LIST = Collections.singletonList("1");

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	public TypeAbstractNumber(Class<T> clazz) {
		super(clazz);
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return TAB_LIST;
	}

}

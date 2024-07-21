package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public interface Type<T> extends Named {
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	// Human friendly name
	String getName();

	Class<T> getClazz();

	// -------------------------------------------- //
	// READ
	// -------------------------------------------- //

	T read(String arg, CommandSender sender) throws KamiCommonException;

	// -------------------------------------------- //
	// TAB LIST
	// -------------------------------------------- //

	// The sender is the one that tried to tab complete.
	// The arg is beginning the word they are trying to tab complete.
	Collection<String> getTabList(CommandSender sender, String arg);

	List<String> getTabListFiltered(CommandSender sender, String arg);

	// -------------------------------------------- //
	// EQUALS
	// -------------------------------------------- //

	boolean equals(T type1, T type2);

	boolean equalsInner(T type1, T type2);

	T createNewInstance();

}

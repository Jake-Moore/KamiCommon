package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.interfaces.Named;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Converts one command argument into a {@code T} and supplies the tab completions offered for it. A
 * {@link com.kamikazejam.kamicommon.command.Parameter Parameter} holds one, and the built-in
 * implementations are singletons reached through a static {@code get()}. Implement this by extending
 * {@link TypeAbstract} rather than directly, which supplies naming, matching and completion filtering.
 *
 * @see <a href="https://github.com/Jake-Moore/KamiCommon/wiki/v5-KamiCommand#types">Types (wiki)</a>
 */
@SuppressWarnings("unused")
public interface Type<T> extends Named {
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //

	// Human friendly name
	@NotNull String getName();

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

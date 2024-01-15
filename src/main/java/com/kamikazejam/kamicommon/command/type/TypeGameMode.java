package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TypeGameMode extends TypeAbstractChoice<GameMode> {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeGameMode i = new TypeGameMode();
	public static TypeGameMode get() { return i; }
	private TypeGameMode() {
		super(GameMode.class);
		this.setAll(GameMode.values());
	}

	// -------------------------------------------- //
	// READ
	// -------------------------------------------- //

	@Override
	public GameMode read(String arg, CommandSender sender) throws KamiCommonException {
		try {
			return GameMode.valueOf(arg.toUpperCase());
		}catch (IllegalArgumentException | NullPointerException e) {
			throw new KamiCommonException().addMsg("<b>No GameMode matches \"<h>%s<b>\".", arg);
		}
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return this.getAll().stream().map(g -> g.name().toLowerCase()).collect(Collectors.toList());
	}
}

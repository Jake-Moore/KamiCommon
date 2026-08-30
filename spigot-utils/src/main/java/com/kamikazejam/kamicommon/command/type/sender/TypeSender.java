package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A {@link CommandSender}, resolved from the console's reserved id, or from a UUID or the exact name of an
 * online player. An argument that matches nothing yields {@code null} rather than an error, so unlike the
 * other sender types this one does not reject an unknown name on the sender's behalf.
 */
public class TypeSender extends TypeAbstract<CommandSender> {

	private static final TypeSender i = new TypeSender();
	public TypeSender() { super(CommandSender.class); }
	public static TypeSender get() {
		return i;
	}


	@Override
	public CommandSender read(String str, CommandSender sender) throws KamiCommonException {
		return IdUtilLocal.getSender(str);
	}

	@Override
	public Collection<String> getTabList(CommandSender commandSender, String s) {
		return commandSender.getServer().getOnlinePlayers().stream().map(Player::getName)
				.filter(key -> key.toLowerCase().startsWith(s.toLowerCase())).limit(20)
				.collect(Collectors.toList());
	}
}
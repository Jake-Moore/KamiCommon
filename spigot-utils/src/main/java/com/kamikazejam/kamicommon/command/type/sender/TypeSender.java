package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Represents a Player currently logged into this server, accessible through the Bukkit API.
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
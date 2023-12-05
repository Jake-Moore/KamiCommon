package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Represents a Player currently logged into this server, accessible through the Bukkit API.
 */
public class TypePlayer extends TypeAbstract<Player> {

	private static final TypePlayer i = new TypePlayer();
	public TypePlayer() { super(Player.class); }
	public static TypePlayer get() {
		return i;
	}


	@Override
	public Player read(String str, CommandSender sender) throws KamiCommonException {
		Player player = sender.getServer().getPlayer(str);
		if (player == null) {
			throw new KamiCommonException().addMsg("<b>No player matching \"<p>%s<b>\".", str);
		}
		return player;
	}

	@Override
	public Collection<String> getTabList(CommandSender commandSender, String s) {
		return commandSender.getServer().getOnlinePlayers().stream().map(Player::getName)
				.filter(key -> key.toLowerCase().startsWith(s.toLowerCase())).limit(20)
				.collect(Collectors.toList());
	}
}
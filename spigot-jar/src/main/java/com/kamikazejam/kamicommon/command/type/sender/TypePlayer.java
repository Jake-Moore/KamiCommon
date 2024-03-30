package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
		Player target = sender.getServer().getPlayer(str);
		if (target == null) {
			throw new KamiCommonException().addMsg("<b>No player matching \"<p>%s<b>\".", str);
		}
		@Nullable PremiumVanishIntegration integration = ((KamiCommon) KamiCommon.get()).getVanishIntegration();
		if (integration != null && sender instanceof Player) {
			Player viewer = (Player) sender;
			if (!integration.canSee(viewer, target)) {
				throw new KamiCommonException().addMsg("<b>No player matching \"<p>%s<b>\".", str);
			}
		}
		return target;
	}

	@Override
	public Collection<String> getTabList(CommandSender commandSender, String s) {
		@Nullable PremiumVanishIntegration integration = ((KamiCommon) KamiCommon.get()).getVanishIntegration();

		return commandSender.getServer().getOnlinePlayers().stream()
				// Filter out vanished players that the sender cannot see
				.filter(plr -> {
					if (!(commandSender instanceof Player)) return true;
					Player viewer = (Player) commandSender;
					return integration == null || integration.canSee(viewer, plr);
				})
				.map(Player::getName)
				.filter(key -> key.toLowerCase().startsWith(s.toLowerCase())).limit(20)
				.collect(Collectors.toList());
	}
}
package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.SpigotUtilProvider;
import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.integrations.PremiumVanishIntegration;
import com.kamikazejam.kamicommon.util.StringUtil;
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
			throw new KamiCommonException().addMsg(StringUtil.t("&cNo player matching \"&3%s&c\"."), str);
		}

		@Nullable PremiumVanishIntegration integration = SpigotUtilProvider.getVanishIntegration();
		if (integration != null && sender instanceof Player viewer) {
            if (!integration.canSee(viewer, target)) {
				throw new KamiCommonException().addMsg(StringUtil.t("&cNo player matching \"&3%s&c\"."), str);
			}
		}
		return target;
	}

	@Override
	public Collection<String> getTabList(CommandSender commandSender, String s) {
		@Nullable PremiumVanishIntegration integration = SpigotUtilProvider.getVanishIntegration();
		return commandSender.getServer().getOnlinePlayers().stream()
				// Filter out vanished players that the sender cannot see
				.filter(plr -> {
					if (!(commandSender instanceof Player viewer)) return true;
                    return integration == null || integration.canSee(viewer, plr);
				})
				.map(Player::getName)
				.filter(key -> key.toLowerCase().startsWith(s.toLowerCase())).limit(20)
				.collect(Collectors.toList());
	}
}
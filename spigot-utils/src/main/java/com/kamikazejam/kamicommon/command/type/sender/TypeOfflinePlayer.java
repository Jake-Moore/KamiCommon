package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.id.SenderPresence;
import com.kamikazejam.kamicommon.util.id.SenderType;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Represents a OfflinePlayer seen before by this server, accessible through the Bukkit API via UUID.
 */
@SuppressWarnings("unused")
public class TypeOfflinePlayer extends TypeAbstract<OfflinePlayer> {

	private static final TypeOfflinePlayer i = new TypeOfflinePlayer();
	public TypeOfflinePlayer() { super(OfflinePlayer.class); }
	public static TypeOfflinePlayer get() {
		return i;
	}


	@Override
	public OfflinePlayer read(String str, CommandSender sender) throws KamiCommonException {
		OfflinePlayer offlinePlayer = IdUtilLocal.getOfflinePlayer(str);
		if (offlinePlayer == null) {
            ChatColor error = KamiCommand.Config.getErrorColor();
            ChatColor param = KamiCommand.Config.getErrorParamColor();
            throw new KamiCommonException().addMsg(LegacyColors.t(error + "No player with the name \"" + param + "%s" + error + "\" has been seen before on this server."), str);
		}
		return offlinePlayer;
	}

	@Override
	public Collection<String> getTabList(CommandSender commandSender, String s) {
		return IdUtilLocal.getNames(SenderPresence.ONLINE, SenderType.PLAYER).stream()
				.filter(key -> key.toLowerCase().startsWith(s.toLowerCase())).limit(20)
				.collect(Collectors.toList());
	}
}
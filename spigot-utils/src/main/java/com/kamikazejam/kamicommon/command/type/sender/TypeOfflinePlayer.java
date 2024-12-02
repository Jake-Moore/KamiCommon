package com.kamikazejam.kamicommon.command.type.sender;

import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.id.SenderPresence;
import com.kamikazejam.kamicommon.util.id.SenderType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Represents a OfflinePlayer seen before by this server, accessible through the Bukkit API via UUID.
 */
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
			throw new KamiCommonException().addMsg(StringUtil.t("&cNo player with the name '&f" + str + "&c' has been seen before on this server."));
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
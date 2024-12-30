package com.kamikazejam.kamicommon.util.mixin;

import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

@SuppressWarnings("unused")
public class MixinPlayed extends Mixin {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final MixinPlayed d = new MixinPlayed();
    @SuppressWarnings("FieldMayBeFinal")
    private static MixinPlayed i = d;

    @Contract(pure = true)
    public static MixinPlayed get() {
        return i;
    }

    // -------------------------------------------- //
    // METHODS
    // -------------------------------------------- //

    public boolean isOnline(Object senderObject) {
        return IdUtilLocal.isOnline(senderObject);
    }

    public boolean isOffline(Object senderObject) {
        return !this.isOnline(senderObject);
    }

    public Long getFirstPlayed(Object senderObject) {
        if (KUtil.isNpc(senderObject)) return null;

        UUID uuid = IdUtilLocal.getUUID(senderObject);
        if (uuid == null) return null;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        Long ret = offlinePlayer.getFirstPlayed();
        if (ret == 0) ret = null;

        return ret;
    }

    public Long getLastPlayed(Object senderObject) {
        //if (this.isOnline(senderObject)) return System.currentTimeMillis();
        // We do in fact NOT want this commented out behavior
        // It's important we can check the previous played time on join!

        if (KUtil.isNpc(senderObject)) return null;

        UUID uuid = IdUtilLocal.getUUID(senderObject);
        if (uuid == null) return null;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        Long ret = offlinePlayer.getLastPlayed();
        if (ret == 0) ret = null;

        return ret;
    }

    public boolean hasPlayedBefore(Object senderObject) {
        Long firstPlayed = this.getFirstPlayed(senderObject);
        return firstPlayed != null && firstPlayed != 0;
    }

}

package com.kamikazejam.kamicommon.util.mixin;

import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

@SuppressWarnings("unused")
public class MixinDisplayName extends Mixin {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final MixinDisplayName d = new MixinDisplayName();
    @SuppressWarnings("FieldMayBeFinal")
    private static MixinDisplayName i = d;

    @Contract(pure = true)
    public static MixinDisplayName get() {
        return i;
    }

    // -------------------------------------------- //
    // METHODS
    // -------------------------------------------- //

    public final static ChatColor DEFAULT_COLOR = ChatColor.WHITE;

    public String getDisplayName(Object senderObject, Object watcherObject) {
        String senderId = IdUtilLocal.getId(senderObject);
        if (senderId == null) return null;

        // Ret
        String ret = null;

        // Bukkit
        Player player = IdUtilLocal.getPlayer(senderObject);
        if (player != null) {
            ret = player.getDisplayName();
        }

        // Fixed Name
        if (ret == null) {
            ret = IdUtilLocal.getName(senderObject).orElse(null);
        }

        // Id Fallback
        if (ret == null) {
            ret = senderId;
        }

        // Ensure Colored
        if (ChatColor.stripColor(ret).equals(ret)) {
            ret = DEFAULT_COLOR.toString() + ret;
        }

        return ret;
    }

}

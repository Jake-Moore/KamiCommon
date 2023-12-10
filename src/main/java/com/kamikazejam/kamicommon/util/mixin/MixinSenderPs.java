package com.kamikazejam.kamicommon.util.mixin;

import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

@SuppressWarnings("unused")
public class MixinSenderPs extends Mixin {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final MixinSenderPs d = new MixinSenderPs();
    @SuppressWarnings("FieldMayBeFinal")
    private static MixinSenderPs i = d;

    @Contract(pure = true)
    public static MixinSenderPs get() {
        return i;
    }

    // -------------------------------------------- //
    // METHODS
    // -------------------------------------------- //

    public PS getSenderPs(Object senderObject) {
        Player player = IdUtilLocal.getPlayer(senderObject);
        if (player == null) return null;
        return PS.valueOf(player.getLocation());
    }

    public void setSenderPs(Object senderObject, PS ps) {
        // Bukkit does not support setting the physical state for offline players for now.
    }

}

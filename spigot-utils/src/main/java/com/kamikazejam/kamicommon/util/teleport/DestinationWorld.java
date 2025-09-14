package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.mixin.MixinWorld;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@SuppressWarnings("unused")
public class DestinationWorld extends DestinationAbstract {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected String worldId;

    public void setWorld(World world) {
        this.worldId = (world == null ? null : world.getName());
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldId);
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationWorld() {

    }

    public DestinationWorld(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        this.setWorld(player.getWorld());
    }

    public DestinationWorld(World world) {
        this.setWorld(world);
    }

    public DestinationWorld(String worldId) {
        this.setWorldId(worldId);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        String worldId = this.getWorldId();
        if (worldId == null) return null;

        return MixinWorld.get().getWorldSpawnPs(worldId);
    }

    @Override
    public @NotNull VersionedComponent getDesc(Object watcherObject) {
        return NmsAPI.getVersionedComponentSerializer().fromMiniMessage("World " + MixinWorld.get().getWorldDisplayNameMini(this.getWorldId()));
    }

}

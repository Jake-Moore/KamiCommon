package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.Txt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PSFormatAbstract implements PSFormat {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final @NotNull VersionedComponent strNull;
    private final @NotNull VersionedComponent strStart;

    private final @NotNull VersionedComponent formatWorld;
    private final @NotNull VersionedComponent formatBlockX;
    private final @NotNull VersionedComponent formatBlockY;
    private final @NotNull VersionedComponent formatBlockZ;
    private final @NotNull VersionedComponent formatLocationX;
    private final @NotNull VersionedComponent formatLocationY;
    private final @NotNull VersionedComponent formatLocationZ;
    private final @NotNull VersionedComponent formatChunkX;
    private final @NotNull VersionedComponent formatChunkZ;
    private final @NotNull VersionedComponent formatPitch;
    private final @NotNull VersionedComponent formatYaw;
    private final @NotNull VersionedComponent formatVelocityX;
    private final @NotNull VersionedComponent formatVelocityY;
    private final @NotNull VersionedComponent formatVelocityZ;

    private final @NotNull VersionedComponent strGlue;
    private final @NotNull VersionedComponent strStop;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public PSFormatAbstract(
            @NotNull VersionedComponent strNull,
            @NotNull VersionedComponent strStart,
            @NotNull VersionedComponent formatWorld,
            @NotNull VersionedComponent formatBlockX,
            @NotNull VersionedComponent formatBlockY,
            @NotNull VersionedComponent formatBlockZ,
            @NotNull VersionedComponent formatLocationX,
            @NotNull VersionedComponent formatLocationY,
            @NotNull VersionedComponent formatLocationZ,
            @NotNull VersionedComponent formatChunkX,
            @NotNull VersionedComponent formatChunkZ,
            @NotNull VersionedComponent formatPitch,
            @NotNull VersionedComponent formatYaw,
            @NotNull VersionedComponent formatVelocityX,
            @NotNull VersionedComponent formatVelocityY,
            @NotNull VersionedComponent formatVelocityZ,
            @NotNull VersionedComponent strGlue,
            @NotNull VersionedComponent strStop
    ) {
        this.strNull = strNull;
        this.strStart = strStart;
        this.formatWorld = formatWorld;
        this.formatBlockX = formatBlockX;
        this.formatBlockY = formatBlockY;
        this.formatBlockZ = formatBlockZ;
        this.formatLocationX = formatLocationX;
        this.formatLocationY = formatLocationY;
        this.formatLocationZ = formatLocationZ;
        this.formatChunkX = formatChunkX;
        this.formatChunkZ = formatChunkZ;
        this.formatPitch = formatPitch;
        this.formatYaw = formatYaw;
        this.formatVelocityX = formatVelocityX;
        this.formatVelocityY = formatVelocityY;
        this.formatVelocityZ = formatVelocityZ;
        this.strGlue = strGlue;
        this.strStop = strStop;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public @NotNull VersionedComponent format(@Nullable PS ps) {
        if (ps == null) return this.strNull;

        List<String> miniMessageEntries = this.formatEntries(ps);

        String miniMessageCenter = Txt.implode(miniMessageEntries, this.strGlue.serializeMiniMessage());
        return this.strStart.append(NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessageCenter)).append(this.strStop);
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    @NotNull
    private List<String> formatEntries(@NotNull PS ps) {
        List<String> ret = new ArrayList<>();

        Object val;

        val = ps.getWorld();
        if (val != null) {
            ret.add(String.format(this.formatWorld.serializeMiniMessage(), val));
        }

        val = ps.getBlockX();
        if (val != null) ret.add(String.format(this.formatBlockX.serializeMiniMessage(), val));

        val = ps.getBlockY();
        if (val != null) ret.add(String.format(this.formatBlockY.serializeMiniMessage(), val));

        val = ps.getBlockZ();
        if (val != null) ret.add(String.format(this.formatBlockZ.serializeMiniMessage(), val));

        val = ps.getLocationX();
        if (val != null) ret.add(String.format(this.formatLocationX.serializeMiniMessage(), val));

        val = ps.getLocationY();
        if (val != null) ret.add(String.format(this.formatLocationY.serializeMiniMessage(), val));

        val = ps.getLocationZ();
        if (val != null) ret.add(String.format(this.formatLocationZ.serializeMiniMessage(), val));

        val = ps.getChunkX();
        if (val != null) ret.add(String.format(this.formatChunkX.serializeMiniMessage(), val));

        val = ps.getChunkZ();
        if (val != null) ret.add(String.format(this.formatChunkZ.serializeMiniMessage(), val));

        val = ps.getPitch();
        if (val != null) ret.add(String.format(this.formatPitch.serializeMiniMessage(), val));

        val = ps.getYaw();
        if (val != null) ret.add(String.format(this.formatYaw.serializeMiniMessage(), val));

        val = ps.getVelocityX();
        if (val != null) ret.add(String.format(this.formatVelocityX.serializeMiniMessage(), val));

        val = ps.getVelocityY();
        if (val != null) ret.add(String.format(this.formatVelocityY.serializeMiniMessage(), val));

        val = ps.getVelocityZ();
        if (val != null) ret.add(String.format(this.formatVelocityZ.serializeMiniMessage(), val));

        return ret;
    }


}

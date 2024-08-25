package com.kamikazejam.kamicommon.library.worldedit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public interface WorldEditApi<C> {

    /**
     * @param rotation The degree rotation (counterClockwise)
     * @return true if the paste was successful (no worldedit pasting errors)
     */
    boolean pasteClipboard(@NotNull World world, @NotNull C clipboard, @NotNull Vector origin, int rotation, int xOffset, int yOffset, int zOffset, boolean flipX, boolean flipZ);

    /**
     * @param rotation The degree rotation (counterClockwise)
     * @return true if the paste was successful (no worldedit pasting errors)
     */
    boolean pasteByFile(@NotNull File file, @NotNull Location location, int rotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;

    @Nullable
    C getClipboardByFile(@NotNull World world, @NotNull File file);

}


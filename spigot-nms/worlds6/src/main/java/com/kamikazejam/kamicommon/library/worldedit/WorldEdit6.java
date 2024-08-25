package com.kamikazejam.kamicommon.library.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class WorldEdit6 implements WorldEditApi<Clipboard> {

    @SuppressWarnings("deprecation")
    //Rotations are counterClockwise
    @Override
    public boolean pasteClipboard(@NotNull World world, @NotNull Clipboard clipboard, org.bukkit.util.@NotNull Vector origin, int rotation, int xOffset, int yOffset, int zOffset, boolean flipX, boolean flipZ) {
        //Normalizes the origin to the min point, not where the player saved it
        clipboard.setOrigin(clipboard.getRegion().getMinimumPoint());

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1);
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, new BukkitWorld(world).getWorldData());
        clipboardHolder.setTransform(new AffineTransform());
        AffineTransform rotate = new AffineTransform();
        rotate = rotate.rotateY(rotation);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(rotate));

        if (flipX) {
            Vector directionW = new Vector(-1, 0, 0);
            AffineTransform flip = new AffineTransform();
            flip = flip.scale(directionW.positive().multiply(-2).add(1, 1, 1));
            clipboardHolder.setTransform(clipboardHolder.getTransform().combine(flip));
        }

        if (flipZ) {
            Vector directionN = new Vector(0, 0, -1);
            AffineTransform flip = new AffineTransform();
            flip = flip.scale(directionN.positive().multiply(-2).add(1, 1, 1));
            clipboardHolder.setTransform(clipboardHolder.getTransform().combine(flip));
        }

        PasteBuilder builder = clipboardHolder.createPaste(editSession, new BukkitWorld(world).getWorldData());
        Vector start = new Vector(origin.getBlockX() + xOffset, origin.getBlockY() + yOffset, origin.getBlockZ() + zOffset);
        Operation operation = builder.to(start).ignoreAirBlocks(true).build();

        try {
            Operations.complete(operation);
            editSession.flushQueue();
            return true;
        } catch (WorldEditException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean pasteByFile(@NotNull File file, @NotNull Location loc, int rotation) {
        if (!file.exists()) { return false; }
        World world = Objects.requireNonNull(loc.getWorld());

        Clipboard clipboard = getClipboardByFile(world, file);
        if (clipboard == null) {
            return false;
        }

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1);
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, new BukkitWorld(world).getWorldData());
        AffineTransform transform = new AffineTransform();
        transform = transform.rotateY(rotation);
        clipboardHolder.setTransform(transform);
        PasteBuilder builder = clipboardHolder.createPaste(editSession, new BukkitWorld(world).getWorldData());
        Operation operation = builder.to(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(true).build();

        try {
            Operations.complete(operation);
            editSession.flushQueue();
            return true;
        } catch (WorldEditException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public @Nullable Clipboard getClipboardByFile(@NotNull World world, @NotNull File file) {
        Clipboard clipboard = null;
        ClipboardFormat format = ClipboardFormat.findByFile(file);
        if (format == null) { return null; }

        try {
            ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
            clipboard = reader.read(new BukkitWorld(world).getWorldData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clipboard;
    }
}

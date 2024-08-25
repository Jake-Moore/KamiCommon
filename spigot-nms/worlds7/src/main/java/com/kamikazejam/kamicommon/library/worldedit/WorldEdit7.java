package com.kamikazejam.kamicommon.library.worldedit;

import com.fastasyncworldedit.core.extent.clipboard.io.FastSchematicReader;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Objects;

public class WorldEdit7 implements WorldEditApi<Clipboard> {

    //Rotations are counterClockwise
    @Override
    public boolean pasteClipboard(org.bukkit.@NotNull World world, @NotNull Clipboard clipboard, @NotNull Vector origin, int rotation, int xOffset, int yOffset, int zOffset, boolean flipX, boolean flipZ) {

        // Normalizes the origin to the min point, not where the player saved it
        BlockVector3 min = clipboard.getRegion().getMinimumPoint();
        clipboard.setOrigin(BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()));

        EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        clipboardHolder.setTransform(new AffineTransform());

        AffineTransform rotate = new AffineTransform();
        rotate = rotate.rotateY(rotation);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(rotate));

        if (flipX) {
            Vector directionW = new Vector(1, 0, 0);
            Vector v = directionW.multiply(-2).add(new Vector(1, 1, 1));

            AffineTransform flip = new AffineTransform();
            flip = flip.scale(v.getX(), v.getY(), v.getZ());
            clipboardHolder.setTransform(clipboardHolder.getTransform().combine(flip));
        }

        if (flipZ) {
            Vector directionN = new Vector(0, 0, 1);
            Vector v = directionN.multiply(-2).add(new Vector(1, 1, 1));

            AffineTransform flip = new AffineTransform();
            flip = flip.scale(v.getX(), v.getY(), v.getZ());
            clipboardHolder.setTransform(clipboardHolder.getTransform().combine(flip));
        }

        BlockVector3 start = BlockVector3.at(origin.getBlockX() + xOffset, origin.getBlockY() + yOffset, origin.getBlockZ() + zOffset);
        PasteBuilder builder = clipboardHolder.createPaste(editSession)
                .to(start)
                .ignoreAirBlocks(true);

        try {
            Operation operation = builder.build();
            Operations.complete(operation);
            editSession.flushQueue();
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean pasteByFile(@NotNull File file, @NotNull Location loc, int rotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (!file.exists()) { return false; }
        org.bukkit.World world = Objects.requireNonNull(loc.getWorld());

        Clipboard clipboard = getClipboardByFile(world, file);
        if (clipboard == null) { return false; }

        Method newEditSession = WorldEdit.getInstance().getClass().getDeclaredMethod("newEditSession", World.class);
        newEditSession.setAccessible(true);

        EditSession editSession = (EditSession) newEditSession.invoke(WorldEdit.getInstance(), new BukkitWorld(world));

        Class<?> clazz = ClipboardHolder.class;
        Constructor<?> c = clazz.getDeclaredConstructor(Clipboard.class);
        ClipboardHolder clipboardHolder = (ClipboardHolder) c.newInstance(clipboard);

        AffineTransform transform = new AffineTransform();
        transform = transform.rotateY(rotation);
        clipboardHolder.setTransform(transform);

        Method createPaste = clipboardHolder.getClass().getDeclaredMethod("createPaste", Extent.class);
        createPaste.setAccessible(true);

        PasteBuilder builder = (PasteBuilder) createPaste.invoke(clipboardHolder, editSession);
        BlockVector3 start = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        Method to = PasteBuilder.class.getDeclaredMethod("to", BlockVector3.class);
        to.setAccessible(true);
        PasteBuilder pasteBuilder = (PasteBuilder) to.invoke(builder, start);
        Operation operation = pasteBuilder.ignoreAirBlocks(true).build();

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
    public @Nullable Clipboard getClipboardByFile(@NotNull org.bukkit.World world, @NotNull File file) {
        Clipboard clipboard;
        Object format = ClipboardFormats.findByFile(file);
        if (format == null) { return null; }

        try {
            Method getReader = format.getClass().getDeclaredMethod("getReader", InputStream.class);
            getReader.setAccessible(true);

            ClipboardReader reader = (ClipboardReader) getReader.invoke(format, Files.newInputStream(file.toPath()));

            // FastSchematicReader has some weird shit
            Method read;
            if (reader instanceof FastSchematicReader) {
                read = reader.getClass().getMethod("read");
            } else {
                read = reader.getClass().getDeclaredMethod("read");
            }
            read.setAccessible(true);
            clipboard = (Clipboard) read.invoke(reader);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return clipboard;
    }
}

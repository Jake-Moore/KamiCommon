package com.kamikazejam.kamicommon.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class DiskUtil {
    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    private final static String UTF8 = "UTF-8";

    // -------------------------------------------- //
    // BYTE
    // -------------------------------------------- //

    public static byte @NotNull [] readBytes(@NotNull File file) throws IOException {
        int length = (int) file.length();
        byte[] output = new byte[length];
        InputStream in = Files.newInputStream(file.toPath());
        int offset = 0;
        // normally it should be able to read the entire file with just a single iteration below, but it depends on the whims of the FileInputStream
        while (offset < length) {
            offset += in.read(output, offset, (length - offset));
        }
        in.close();
        return output;
    }

    public static void writeBytes(@NotNull File file, byte @NotNull [] bytes) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    // -------------------------------------------- //
    // STRING
    // -------------------------------------------- //

    public static void write(@NotNull File file, @NotNull String content) throws IOException {
        writeBytes(file, utf8(content));
    }

    @Contract("_ -> new")
    public static @NotNull String read(@NotNull File file) throws IOException {
        return utf8(readBytes(file));
    }

    // -------------------------------------------- //
    // CATCH
    // -------------------------------------------- //

    public static boolean writeCatch(File file, String content) {
        try {
            write(file, content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Contract("null -> null")
    public static @Nullable String readCatch(File file) {
        try {
            return read(file);
        } catch (IOException e) {
            return null;
        }
    }

    // -------------------------------------------- //
    // FILE DELETION
    // -------------------------------------------- //

    public static boolean deleteRecursive(@NotNull File path) throws FileNotFoundException {
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files == null) { return true; }

            for (File f : files) {
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

    // -------------------------------------------- //
    // UTF8 ENCODE AND DECODE
    // -------------------------------------------- //

    @Contract(pure = true)
    public static byte @NotNull [] utf8(@NotNull String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}


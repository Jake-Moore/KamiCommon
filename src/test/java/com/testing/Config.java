package com.testing;

import com.kamikazejam.kamicommon.configuration.config.StandaloneConfig;

import java.io.File;

public class Config {

    public static void main(String[] args) {
        StandaloneConfig config = new StandaloneConfig(new File("C:\\Users\\Jake\\Desktop\\config.yml"), true);
        config.setDefaultCommentsOverwrite(true);

        int i = 43;
        double d = 43.0;
        long l = 430000000000L;
        float f = 43.0F;
        short s = (short) 43;
        byte b = (byte) 43;
        boolean bool = true;

        config.setInt("numbers.int", i);
        config.setDouble("numbers.double", d);
        config.setLong("numbers.long", l);
        config.setFloat("numbers.float", f);
        config.setShort("numbers.short", s);
        config.setByte("numbers.byte", b);
        config.setBoolean("numbers.boolean", bool);

        config.save();
        config.reload();

        int ci = config.getInt("numbers.int");
        double cd = config.getDouble("numbers.double");
        long cl = config.getLong("numbers.long");
        float cf = config.getFloat("numbers.float");
        short cs = config.getShort("numbers.short");
        byte cb = config.getByte("numbers.byte");
        boolean cBool = config.getBoolean("numbers.boolean");

        assert ci == i;
        assert cd == d;
        assert cl == l;
        assert cf == f;
        assert cs == s;
        assert cb == b;
        assert cBool == bool;

        System.out.println("All tests passed!");

        System.out.println("Int: " + ci);
        System.out.println("Double: " + cd);
        System.out.println("Long: " + cl);
        System.out.println("Float: " + cf);
        System.out.println("Short: " + cs);
        System.out.println("Byte: " + cb);
        System.out.println("Boolean: " + true);

        // System.out.println("Keys: " + config.getConfigurationSection("levels").getKeys(false));
    }
}
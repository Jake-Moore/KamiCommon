package com.kamikazejam.kamicommon.yaml;

import com.kamikazejam.kamicommon.yaml.spigot.ItemStackHelper;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class ConfigurationSection {
    @Getter @Nullable private final ItemStackHelper itemStackHelper;
    public ConfigurationSection() {
        ItemStackHelper temp; //IDK why intelliJ got mad lol
        try {
            Class.forName("org.bukkit.inventory.ItemStack");
            temp = new ItemStackHelper(this);
        } catch (ClassNotFoundException e) {
            temp = null;
        }
        itemStackHelper = temp;
    }

    public abstract void set(String key, Object value);
    public abstract void put(String key, Object value);
    public abstract Object get(String key);
    public abstract Object get(String key, Object def);
    public abstract void putString(String key, String value);
    public abstract void putBoolean(String key, boolean value);
    public abstract void putByte(String key, byte value);
    public abstract void putShort(String key, short value);
    public abstract void putInteger(String key, int value);
    public abstract void putInt(String key, int value);
    public abstract void putLong(String key, long value);
    public abstract void putDouble(String key, double value);
    public abstract void putFloat(String key, float value);


    public abstract void setString(String key, String value);
    public abstract void setBoolean(String key, boolean value);
    public abstract void setByte(String key, byte value);
    public abstract void setShort(String key, short value);
    public abstract void setInteger(String key, int value);
    public abstract void setInt(String key, int value);
    public abstract void setLong(String key, long value);
    public abstract void setDouble(String key, double value);
    public abstract void setFloat(String key, float value);


    public abstract MemoryConfiguration getConfigurationSection(String key);
    public abstract String getString(String key);
    public abstract String getString(String key, String def);
    public abstract boolean isString(String key);
    public abstract int getInt(String key);
    public abstract int getInt(String key, int def);
    public abstract boolean isInt(String key);
    public abstract boolean getBoolean(String key);
    public abstract boolean getBoolean(String key, boolean def);
    public abstract boolean isBoolean(String key);
    public abstract double getDouble(String key);
    public abstract double getDouble(String key, double def);
    public abstract boolean isDouble(String key);
    public abstract byte getByte(String key);
    public abstract byte getByte(String key, byte def);
    public abstract boolean isByte(String key);
    public abstract short getShort(String key);
    public abstract short getShort(String key, short def);
    public abstract boolean isShort(String key);
    public abstract float getFloat(String key);
    public abstract float getFloat(String key, float def);
    public abstract boolean isFloat(String key);
    public abstract long getLong(String key);
    public abstract long getLong(String key, long def);
    public abstract boolean isLong(String key);
    public abstract List<?> getList(String key);
    public abstract List<?> getList(String key, List<?> def);
    public abstract boolean isList(String key);
    public abstract List<String> getStringList(String key);
    public abstract List<String> getStringList(String key, List<String> def);
    public abstract List<Integer> getIntegerList(String key);
    public abstract List<Integer> getIntegerList(String key, List<Integer> def);
    public abstract List<Byte> getByteList(String key);
    public abstract List<Byte> getByteList(String key, List<Byte> def);
    public abstract Set<String> getKeys(boolean deep);
    public abstract boolean isConfigurationSection(String key);
    public abstract boolean contains(String key);
    public abstract boolean isSet(String key);

    public abstract void addDefault(String key, Object o);

    public abstract boolean isEmpty();

    public abstract Object getItemStack(String key);
    public abstract Object getItemStack(String key, Object def);
    public abstract void setItemStack(String key, Object item);

    public abstract void setItemBuilder(String key, Object builder);
}

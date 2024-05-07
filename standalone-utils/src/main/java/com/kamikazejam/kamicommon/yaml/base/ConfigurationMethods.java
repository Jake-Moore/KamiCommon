package com.kamikazejam.kamicommon.yaml.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface ConfigurationMethods<T extends ConfigurationMethods<?>> {
    void set(String key, Object value);
    void put(String key, Object value);
    Object get(String key);
    Object get(String key, Object def);
    void putString(String key, String value);
    void putBoolean(String key, boolean value);
    void putByte(String key, byte value);
    void putShort(String key, short value);
    void putInteger(String key, int value);
    void putInt(String key, int value);
    void putLong(String key, long value);
    void putDouble(String key, double value);
    void putFloat(String key, float value);


    void setString(String key, String value);
    void setBoolean(String key, boolean value);
    void setByte(String key, byte value);
    void setShort(String key, short value);
    void setInteger(String key, int value);
    void setInt(String key, int value);
    void setLong(String key, long value);
    void setDouble(String key, double value);
    void setFloat(String key, float value);


    @NotNull T getConfigurationSection(String key);
    String getString(String key);
    String getString(String key, String def);
    boolean isString(String key);
    int getInt(String key);
    int getInt(String key, int def);
    boolean isInt(String key);
    boolean getBoolean(String key);
    boolean getBoolean(String key, boolean def);
    boolean isBoolean(String key);
    double getDouble(String key);
    double getDouble(String key, double def);
    boolean isDouble(String key);
    byte getByte(String key);
    byte getByte(String key, byte def);
    boolean isByte(String key);
    short getShort(String key);
    short getShort(String key, short def);
    boolean isShort(String key);
    float getFloat(String key);
    float getFloat(String key, float def);
    boolean isFloat(String key);
    long getLong(String key);
    long getLong(String key, long def);
    boolean isLong(String key);
    List<?> getList(String key);
    List<?> getList(String key, List<?> def);
    boolean isList(String key);
    List<String> getStringList(String key);
    List<String> getStringList(String key, List<String> def);
    List<Integer> getIntegerList(String key);
    List<Integer> getIntegerList(String key, List<Integer> def);
    List<Byte> getByteList(String key);
    List<Byte> getByteList(String key, List<Byte> def);
    Set<String> getKeys(boolean deep);
    boolean isConfigurationSection(String key);
    boolean contains(String key);
    boolean isSet(String key);

    void addDefault(String key, Object o);

    boolean isEmpty();
}

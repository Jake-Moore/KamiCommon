package com.kamikazejam.kamicommon.yaml.base;

import com.kamikazejam.kamicommon.yaml.AbstractMemorySection;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.yaml.snakeyaml.nodes.MappingNode;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class MemorySectionMethods<T extends AbstractMemorySection<?>> extends AbstractMemorySection<T> {

    public MemorySectionMethods(@Nullable MappingNode node) {
        super(node);
    }

    public Object get(String key) {
        return getObject(getNode(), key);
    }

    public Object get(String key, Object def) {
        if (contains(key)) {
            return get(key);
        } else {
            return def;
        }
    }

    public void putString(String key, String value) {
        put(key, value);
    }

    public void setString(String key, String value) {
        put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        put(key, value);
    }

    public void setBoolean(String key, boolean value) {
        put(key, value);
    }

    public void putInteger(String key, int value) {
        put(key, value);
    }

    public void putInt(String key, int value) {
        put(key, value);
    }

    public void setInteger(String key, int value) {
        put(key, value);
    }

    public void setInt(String key, int value) {
        put(key, value);
    }


    public void putLong(String key, long value) {
        put(key, value);
    }

    public void setLong(String key, long value) {
        put(key, value);
    }

    public void putDouble(String key, double value) {
        put(key, value);
    }

    public void setDouble(String key, double value) {
        put(key, value);
    }

    public void putFloat(String key, float value) {
        put(key, value);
    }

    public void setFloat(String key, float value) {
        put(key, value);
    }

    public void putByte(String key, byte value) {
        put(key, value);
    }

    public void setByte(String key, byte value) {
        put(key, value);
    }

    public void putShort(String key, short value) {
        put(key, value);
    }

    public void setShort(String key, short value) {
        put(key, value);
    }


    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String def) {
        Object val = get(key, def);
        return (val != null) ? val.toString() : def;
    }

    public boolean isString(String key) {
        return get(key) instanceof String;
    }


    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        Object val = get(key, def);
        if (val == null) {
            return def;
        }
        if (val instanceof Boolean) {
            return (boolean) val;
        }
        if (val instanceof String) {
            String s = (String) val;
            if (s.equalsIgnoreCase("true")) {
                return true;
            }
            if (s.equalsIgnoreCase("false")) {
                return false;
            }
            if (s.equalsIgnoreCase("yes")) {
                return true;
            }
            if (s.equalsIgnoreCase("no")) {
                return false;
            }
            if (s.equalsIgnoreCase("on")) {
                return true;
            }
            if (s.equalsIgnoreCase("off")) {
                return false;
            }
        }
        return def;
    }

    public boolean isBoolean(String key) {
        return get(key) instanceof Boolean;
    }


    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(String key, byte def) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return def;
        }
        if (!isByte(key)) {
            return def;
        }
        return bd.byteValue();
    }

    public boolean isByte(String key) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return false;
        }
        return (bd.doubleValue() <= Byte.MAX_VALUE && bd.doubleValue() >= Byte.MIN_VALUE);
    }


    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(String key, short def) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return def;
        }
        if (!isShort(key)) {
            return def;
        }
        return bd.shortValue();
    }

    public boolean isShort(String key) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return false;
        }
        return (bd.doubleValue() <= Short.MAX_VALUE && bd.doubleValue() >= Short.MIN_VALUE);
    }


    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return def;
        }
        if (!isInt(key)) {
            return def;
        }
        return bd.intValue();
    }

    public boolean isInt(String key) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return false;
        }
        return (bd.doubleValue() <= Integer.MAX_VALUE && bd.doubleValue() >= Integer.MIN_VALUE);
    }


    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long def) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return def;
        }
        if (!isLong(key)) {
            return def;
        }
        return bd.longValue();
    }

    public boolean isLong(String key) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return false;
        }
        return (bd.doubleValue() <= Long.MAX_VALUE && bd.doubleValue() >= Long.MIN_VALUE);
    }


    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(String key, float def) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return def;
        }
        if (!isFloat(key)) {
            return def;
        }
        return bd.floatValue();
    }

    public boolean isFloat(String key) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return false;
        }
        return (Math.abs(bd.doubleValue()) <= Float.MAX_VALUE && Math.abs(bd.doubleValue()) >= Float.MIN_VALUE);
    }


    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public double getDouble(String key, double def) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return def;
        }
        if (!isDouble(key)) {
            return def;
        }
        return bd.doubleValue();
    }

    public boolean isDouble(String key) {
        BigDecimal bd = getNumberAt(key);
        if (bd == null) {
            return false;
        }
        return (Math.abs(bd.doubleValue()) <= Double.MAX_VALUE && Math.abs(bd.doubleValue()) >= Double.MIN_VALUE);
    }

    private @Nullable BigDecimal getNumberAt(String key) {
        String s = getString(key);
        if (s == null) {
            return null;
        }

        return getBigDecimal(s);
    }


    public List<?> getList(String key) {
        return getList(key, null);
    }

    public List<?> getList(String key, final List<?> def) {
        Object val = get(key, def);
        return (List<?>) ((val instanceof List) ? val : def);
    }

    public boolean isList(String key) {
        return get(key) instanceof List;
    }


    public List<String> getStringList(String key) {
        return getStringList(key, new ArrayList<>());
    }

    public List<String> getStringList(String key, List<String> def) {
        final List<?> list = getList(key);
        if (list == null) {
            return def;
        }

        final List<String> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof String || this.isPrimitiveWrapper(object)) {
                result.add(String.valueOf(object));
            }
        }
        return result;
    }


    public List<Integer> getIntegerList(String key) {
        return getIntegerList(key, new ArrayList<>());
    }

    public List<Integer> getIntegerList(String key, List<Integer> def) {
        List<?> list = getList(key);
        if (list == null) {
            return def;
        }

        final List<Integer> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer) object);
            } else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String) object));
                } catch (Exception ignored) {
                }
            } else if (object instanceof Character) {
                result.add((int) (char) object);
            } else {
                if (!(object instanceof Number)) {
                    continue;
                }
                result.add(((Number) object).intValue());
            }
        }
        return result;
    }


    public List<Byte> getByteList(String key) {
        return getByteList(key, new ArrayList<>());
    }

    public List<Byte> getByteList(String key, List<Byte> def) {
        List<?> list = getList(key);
        if (list == null) {
            return def;
        }

        final List<Byte> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof Integer) {
                if (((Integer) object) >= -128 && ((Integer) object) <= 127) {
                    result.add(((Integer) object).byteValue());
                }
            } else if (object instanceof String || object instanceof Character) {
                try {
                    Byte.valueOf(object.toString());
                } catch (Exception ignored) {
                }
            } else {
                if (!(object instanceof Number)) {
                    continue;
                }
                int i = ((Number) object).intValue();
                if (i >= -128 && i <= 127) {
                    result.add(((Number) object).byteValue());
                }
            }
        }
        return result;
    }

    /**
     * Returns the keys of the config
     * If Deep is enabled, it will dig and find all valid keys that resolve to a value
     *
     * @param deep Whether to search for all sub-keys
     * @return The list of keys found
     */

    public Set<String> getKeys(boolean deep) {
        return getKeys(getNode(), deep);
    }


    public boolean isConfigurationSection(final String key) {
        return get(key) instanceof MappingNode;
    }


    public boolean contains(String key) {
        return contains(getNode(), key, "");
    }

    public boolean isSet(String key) {
        return contains(key);
    }


    public void addDefault(String key, Object o) {
        if (contains(key)) {
            return;
        }
        put(key, o);
    }

    public boolean isEmpty() {
        if (getNode().getValue() == null) {
            return true;
        }
        return getNode().getValue().isEmpty();
    }


    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    public boolean save(File f) {
        if (!isChanged()) { return false; }

        try {
            // Dump the Node (should keep comments)
            Writer writer = new OutputStreamWriter(Files.newOutputStream(f.toPath()), StandardCharsets.UTF_8);
            YamlUtil.getYaml().serialize(this.getNode(), writer);
            setChanged(false);
            return true;

        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

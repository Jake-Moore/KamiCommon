package com.kamikazejamplugins.kamicommon.yaml;

import lombok.Getter;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings({"unchecked", "unused"})
public abstract class MemorySection extends ConfigurationSection {
    @Getter
    private final LinkedHashMap<String, Object> data;
    public MemorySection(LinkedHashMap<String, Object> data) {
        this.data = data;
    }

    @Override
    public void set(String key, Object value) { put(key, value); }

    @Override
    public void put(String key, Object value) {
        //ItemStacks are special
        if (getItemStackHelper() != null && getItemStackHelper().isStack(value)) {
            getItemStackHelper().setItemStack(key, value); return;
        }

        if (value instanceof Byte) { value = value + "b"; }
        if (value instanceof Short) { value = value + "s"; }
        if (value instanceof Float) { value = value + "f"; }
        if (value instanceof Double) { value = value + "D"; }

        String[] keys = key.split("\\.");
        if (keys.length <= 1) { data.put(key, value); return; }

        LinkedHashMap<String, Object> dataRecent = data;
        for (int i = 0; i < keys.length-1; i++) {
            String k = keys[i];
            if (dataRecent.containsKey(k)) {
                if (dataRecent.get(k) instanceof LinkedHashMap) {
                    dataRecent = (LinkedHashMap<String, Object>) dataRecent.get(k);
                }else {
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j <= i; j++) {
                        builder.append(keys[j]).append(" ");
                    }
                    String newKey = builder.toString().trim().replaceAll(" ", ".");
                    if (key.equalsIgnoreCase(newKey)) {
                        System.out.println(YamlHandler.ANSI.RED + "Equal Keys: " + key);
                    }
                    put(newKey, new LinkedHashMap<>());

                    dataRecent = new LinkedHashMap<>();
                }
            }else if (i != keys.length - 1){
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j <= i; j++) {
                    builder.append(keys[j]).append(" ");
                }
                String newKey = builder.toString().trim().replaceAll(" ", ".");
                put(newKey, new LinkedHashMap<>());

                dataRecent = new LinkedHashMap<>();
            }
        }
        dataRecent.put(keys[keys.length - 1], value);

        LinkedHashMap<String, Object> newData = dataRecent;
        newData.put(keys[keys.length - 1], value);
        for (int j = keys.length - 2; j >= 1; j--){
            LinkedHashMap<String, Object> temp = data;
            for (int k = 0; k < j; k++) {
                temp = (LinkedHashMap<String, Object>) temp.get(keys[k]);
            }
            temp.put(keys[j], newData);
            newData = temp;
        }

        data.put(keys[0], newData);
    }

    @Override
    public @Nullable Object get(String key) {
        String[] keys = key.split("\\.");
        LinkedHashMap<String, Object> map = data;
        for (int i = 0; i < keys.length-1; i++) {
            if (map == null) { return null; }

            if (map.containsKey(keys[i])) {
                map = (LinkedHashMap<String, Object>) map.get(keys[i]);
            } else {
                return null;
            }
        }
        if (map == null) { return null; }
        return map.get(keys[keys.length-1]);
    }

    @Override
    public Object get(String key, Object def) {
        if (contains(key)) { return get(key);
        }else { return def; }
    }

    @Override public void putString(String key, String value) { put(key, value); }
    @Override public void setString(String key, String value) { put(key, value); }

    @Override public void putBoolean(String key, boolean value) { put(key, value); }
    @Override public void setBoolean(String key, boolean value) { put(key, value); }

    @Override public void putInteger(String key, int value) { put(key, value); }
    @Override public void putInt(String key, int value) { put(key, value); }

    @Override public void setInteger(String key, int value) { put(key, value); }
    @Override public void setInt(String key, int value) { put(key, value); }


    @Override public void putLong(String key, long value) { put(key, value); }
    @Override public void setLong(String key, long value) { put(key, value); }

    @Override public void putDouble(String key, double value) { put(key, value); }
    @Override public void setDouble(String key, double value) { put(key, value); }

    @Override public void putFloat(String key, float value) { put(key, value); }
    @Override public void setFloat(String key, float value) { put(key, value); }

    @Override public void putByte(String key, byte value) { put(key, value); }
    @Override public void setByte(String key, byte value) { put(key, value); }

    @Override public void putShort(String key, short value) { put(key, value); }
    @Override public void setShort(String key, short value) { put(key, value); }

    @Override
    public MemoryConfiguration getConfigurationSection(String key) {
        Object o = get(key);
        if (!(o instanceof LinkedHashMap)) {
            return new MemoryConfiguration(new LinkedHashMap<>());
        }
        return new MemoryConfiguration((LinkedHashMap<String, Object>) o);
    }

    @Override
    public String getString(String key) { return getString(key, null); }
    @Override
    public String getString(String key, String def) {
        Object val = get(key, def);
        return (val != null) ? val.toString() : def;
    }
    @Override
    public boolean isString(String key) { return get(key) instanceof String; }


    @Override
    public boolean getBoolean(String key) { return getBoolean(key, false); }
    @Override
    public boolean getBoolean(String key, boolean def) {
        Object val = get(key, def);
        return (boolean)((val instanceof Boolean) ? val : def);
    }
    @Override
    public boolean isBoolean(String key) { return get(key) instanceof Boolean; }





    @Override
    public byte getByte(String key) { return getByte(key, (byte) 0); }
    @Override
    public byte getByte(String key, byte def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isByte(key)) { return def; }
        return bd.byteValue();
    }
    @Override
    public boolean isByte(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Byte.MAX_VALUE && bd.doubleValue() >= Byte.MIN_VALUE);
    }



    @Override
    public short getShort(String key) { return getShort(key, (short) 0); }
    @Override
    public short getShort(String key, short def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isShort(key)) { return def; }
        return bd.shortValue();
    }
    @Override
    public boolean isShort(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Short.MAX_VALUE && bd.doubleValue() >= Short.MIN_VALUE);
    }



    @Override
    public int getInt(String key) { return getInt(key, 0); }
    @Override
    public int getInt(String key, int def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isInt(key)) { return def; }
        return bd.intValue();
    }
    @Override
    public boolean isInt(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Integer.MAX_VALUE && bd.doubleValue() >= Integer.MIN_VALUE);
    }



    @Override
    public long getLong(String key) { return getLong(key, 0L); }
    @Override
    public long getLong(String key, long def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isLong(key)) { return def; }
        return bd.longValue();
    }
    @Override
    public boolean isLong(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (bd.doubleValue() <= Long.MAX_VALUE && bd.doubleValue() >= Long.MIN_VALUE);
    }



    @Override
    public float getFloat(String key) { return getFloat(key, 0f); }
    @Override
    public float getFloat(String key, float def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isFloat(key)) { return def; }
        return bd.floatValue();
    }
    @Override
    public boolean isFloat(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (Math.abs(bd.doubleValue()) <= Float.MAX_VALUE && Math.abs(bd.doubleValue()) >= Float.MIN_VALUE);
    }



    @Override
    public double getDouble(String key) { return getDouble(key, 0.0); }
    @Override
    public double getDouble(String key, double def) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return def; }
        if (!isDouble(key)) { return def; }
        return bd.doubleValue();
    }
    @Override
    public boolean isDouble(String key) {
        BigDecimal bd = getNumberAt(key); if (bd == null) { return false; }
        return (Math.abs(bd.doubleValue()) <= Double.MAX_VALUE && Math.abs(bd.doubleValue()) >= Double.MIN_VALUE);
    }



    private @Nullable BigDecimal getNumberAt(String key) {
        String s = getString(key);
        if (s == null) { return null; }

        // If it's any of the following, remove the last character
        if (s.endsWith("D") || s.endsWith("f") || s.endsWith("s") || s.endsWith("b")) {
            String sub = s.substring(0, s.length() - 1);
            try { return new BigDecimal(sub);
            }catch (Exception ignored) {}
        }else {
            try { return new BigDecimal(s);
            }catch (Exception ignored) {}
        }
        return null;
    }








    @Override
    public List<?> getList(String key) { return getList(key, null); }
    @Override
    public List<?> getList(String key, final List<?> def) {
        Object val = get(key, def);
        return (List<?>)((val instanceof List) ? val : def);
    }
    @Override
    public boolean isList(String key) { return get(key) instanceof List; }

    @Override
    public List<String> getStringList(String key) { return getStringList(key, new ArrayList<>()); }
    @Override
    public List<String> getStringList(String key, List<String> def) {
        final List<?> list = getList(key);
        if (list == null) { return def; }

        final List<String> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof String || this.isPrimitiveWrapper(object)) {
                result.add(String.valueOf(object));
            }
        }
        return result;
    }




    @Override
    public List<Integer> getIntegerList(String key) {
        return getIntegerList(key, new ArrayList<>());
    }
    @Override
    public List<Integer> getIntegerList(String key, List<Integer> def) {
        List<?> list = getList(key);
        if (list == null) { return def; }

        final List<Integer> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer)object);
            }
            else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String)object));
                } catch (Exception ignored) {}
            }
            else if (object instanceof Character) {
                result.add((int)(char)object);
            }
            else {
                if (!(object instanceof Number)) { continue; }
                result.add(((Number)object).intValue());
            }
        }
        return result;
    }



    @Override
    public List<Byte> getByteList(String key) {
        return getByteList(key, new ArrayList<>());
    }
    @Override
    public List<Byte> getByteList(String key, List<Byte> def) {
        List<?> list = getList(key);
        if (list == null) { return def; }

        final List<Byte> result = new ArrayList<>();
        for (final Object object : list) {
            if (object instanceof Integer) {
                if (((Integer)object) >= -128 && ((Integer)object) <= 127) {
                    result.add(((Integer)object).byteValue());
                }
            }
            else if (object instanceof String || object instanceof Character) {
                try {
                    Byte.valueOf(object.toString());
                } catch (Exception ignored) {}
            }
            else {
                if (!(object instanceof Number)) { continue; }
                int i = ((Number)object).intValue();
                if (i >= -128 && i <= 127) {
                    result.add(((Number)object).byteValue());
                }
            }
        }
        return result;
    }

    /**
     * Returns the keys of the config
     * If Deep is enabled, it will dig and find all valid keys that resolve to a value
     * @param deep Whether to search for all sub-keys
     * @return The list of keys found
     */
    @Override
    public Set<String> getKeys(boolean deep) {
        if (!deep) {
            return data.keySet();
        }else {
            Set<String> keys = new HashSet<>();

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getValue() instanceof LinkedHashMap) {
                    for (String k : getConfigurationSection(entry.getKey()).getKeys(true)) {
                        keys.add(entry.getKey() + "." + k);
                    }
                }else {
                    keys.add(entry.getKey());
                }
            }
            return keys;
        }
    }

    @Override
    public boolean isConfigurationSection(final String key) {
        return get(key) instanceof LinkedHashMap;
    }

    @Override
    public boolean contains(String key) {
        String[] keys = key.split("\\.");

        LinkedHashMap<String, Object> map = data;
        for (int i = 0; i < keys.length-1; i++) {
            if (map.containsKey(keys[i]) && map.get(keys[i]) instanceof LinkedHashMap) {
                map = (LinkedHashMap<String, Object>) map.get(keys[i]);
            } else {
                return false;
            }
        }
        return map.containsKey(keys[keys.length - 1]);
    }

    @Override
    public boolean isSet(String key) { return contains(key); }

    @Override
    public void addDefault(String key, Object o) {
        if (contains(key)) { return; }
        set(key, o);
    }

    protected boolean isPrimitiveWrapper(final Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }


    @Override
    public Object getItemStack(String key) {
        if (getItemStackHelper() == null) { return null; }
        return getItemStackHelper().getItemStack(key);
    }

    @Override
    public Object getItemStack(String key, Object def) {
        if (getItemStackHelper() == null) { return def; }
        return getItemStackHelper().getItemStack(key, def);
    }

    @Override
    public void setItemStack(String key, Object item) {
        if (getItemStackHelper() == null) { return; }
        getItemStackHelper().setItemStack(key, item);
    }
}

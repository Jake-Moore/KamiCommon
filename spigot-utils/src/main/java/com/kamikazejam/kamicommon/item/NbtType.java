package com.kamikazejam.kamicommon.item;

import com.kamikazejam.kamicommon.nbtapi.iface.ReadWriteItemNBT;
import com.kamikazejam.kamicommon.nbtapi.iface.ReadableNBT;
import com.kamikazejam.kamicommon.yaml.base.ConfigurationMethods;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import com.kamikazejam.kamicommon.nbtapi.NBTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "unchecked", "unused"})
public enum NbtType {
    STRING(ConfigurationMethods::getString, (nbt, k, o) -> nbt.setString(k, (String) o), NBTType.NBTTagString,
            ReadableNBT::getString, "str", "string"),
    BOOLEAN(ConfigurationMethods::getBoolean, (nbt, k, o) -> nbt.setBoolean(k, (boolean) o), null,
            ReadableNBT::getBoolean, "bool", "boolean"),
    BYTE(ConfigurationMethods::getByte, (nbt, k, o) -> nbt.setByte(k, (byte) o), NBTType.NBTTagByte,
            ReadableNBT::getByte, "byte"),
    SHORT(ConfigurationMethods::getShort, (nbt, k, o) -> nbt.setShort(k, (short) o), NBTType.NBTTagShort,
            ReadableNBT::getShort, "short"),
    INTEGER(ConfigurationMethods::getInt, (nbt, k, o) -> nbt.setInteger(k, (int) o), NBTType.NBTTagInt,
            ReadableNBT::getInteger, "int", "integer"),
    LONG(ConfigurationMethods::getLong, (nbt, k, o) -> nbt.setLong(k, (long) o), NBTType.NBTTagLong,
            ReadableNBT::getLong, "long"),
    FLOAT(ConfigurationMethods::getFloat, (nbt, k, o) -> nbt.setFloat(k, (float) o), NBTType.NBTTagFloat,
            ReadableNBT::getFloat, "float"),
    DOUBLE(ConfigurationMethods::getDouble, (nbt, k, o) -> nbt.setDouble(k, (double) o), NBTType.NBTTagDouble,
            ReadableNBT::getDouble, "double"),
    BYTE_ARRAY(ConfigurationMethods::getByteList, (nbt, k, o) -> nbt.setByteArray(k, convertByteList((List<Byte>) o)), NBTType.NBTTagByteArray,
            ReadableNBT::getByteArray, "byte array", "bytearray", "byte_array"),
    INT_ARRAY(ConfigurationMethods::getIntegerList, (nbt, k, o) -> nbt.setIntArray(k, convertIntList((List<Integer>) o)), NBTType.NBTTagIntArray,
            ReadableNBT::getIntArray, "int array", "intarray", "int_array"),
    UUID((c, k) -> java.util.UUID.fromString(c.getString(k)), (nbt, k, o) -> nbt.setUUID(k, (java.util.UUID) o), null,
            ReadableNBT::getUUID, "uuid"),
    ;

    public interface ConfRead {
        @NotNull Object read(ConfigurationSection section, String key);
    }
    public interface NbtWrite {
        void write(ReadWriteItemNBT item, String key, Object o);
    }
    public interface NbtRead {
        Object read(ReadableNBT item, String key);
    }

    public static final NbtType[] CACHE = values();

    private final String[] names;
    private final ConfRead confRead;
    private final NbtWrite write;
    private final NbtRead read;
    private final NBTType type;
    NbtType(ConfRead confRead, NbtWrite write, NBTType type, NbtRead read, String... names) {
        this.confRead = confRead;
        this.write = write;
        this.type = type;
        this.read = read;
        this.names = names;
    }

    public @NotNull Object readConf(ConfigurationSection section, String key) {
        return confRead.read(section, key);
    }
    public void write(ReadWriteItemNBT item, String key, Object o) {
        write.write(item, key, o);
    }
    public @NotNull Object read(ReadableNBT item, String key) {
        return read.read(item, key);
    }

    public static @NotNull NbtType fromName(String name) {
        for (NbtType type : CACHE) {
            for (String n : type.names) {
                if (n.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Unknown NBT type: " + name);
    }

    public static byte[] convertByteList(List<Byte> list) {
        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
    public static int[] convertIntList(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static @Nullable NbtType matchNBTAPI(NBTType type) {
        for (NbtType nbtType : CACHE) {
            if (nbtType.type == type) {
                return nbtType;
            }
        }
        return null;
    }
}

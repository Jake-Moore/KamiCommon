package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.type.primitive.*;
import com.kamikazejam.kamicommon.command.type.sender.TypeOfflinePlayer;
import com.kamikazejam.kamicommon.command.type.sender.TypePlayer;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

// TODO not sure what this does tbh, now that its been stripped down a lot
@SuppressWarnings("unused")
public class RegistryType {
    // -------------------------------------------- //
    // REGISTRY
    // -------------------------------------------- //

    private static final Map<Class<?>, Type<?>> registry = new HashMap<>();

    @Contract("null, _ -> fail; !null, null -> fail")
    public static <T> void register(Class<T> clazz, Type<? super T> type) {
        if (clazz == null) throw new NullPointerException("clazz");
        if (type == null) throw new NullPointerException("type");
        registry.put(clazz, type);
    }

    @Contract("null -> fail")
    public static <T> void register(Type<T> type) {
        if (type == null) throw new NullPointerException("type");
        register(type.getClazz(), type);
    }

    @Contract("null -> fail")
    @SuppressWarnings("unchecked")
    public static <T> Type<? super T> unregister(Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("clazz");
        return (Type<T>) registry.remove(clazz);
    }

    @Contract("null -> fail")
    public static boolean isRegistered(Class<?> clazz) {
        if (clazz == null) throw new NullPointerException("clazz");
        return registry.containsKey(clazz);
    }

    // -------------------------------------------- //
    // DEFAULTS
    // -------------------------------------------- //

    public static void registerAll() {
        // Primitive
        register(Boolean.TYPE, TypeBooleanTrue.get());
        register(Boolean.class, TypeBooleanTrue.get());

        register(Byte.TYPE, TypeByte.get());
        register(Byte.class, TypeByte.get());

        register(Double.TYPE, TypeDouble.get());
        register(Double.class, TypeDouble.get());

        register(Float.TYPE, TypeFloat.get());
        register(Float.class, TypeFloat.get());

        register(Integer.TYPE, TypeInteger.get());
        register(Integer.class, TypeInteger.get());

        register(Long.TYPE, TypeLong.get());
        register(Long.class, TypeLong.get());

        register(TypeString.get());


        // Bukkit
        register(TypeUUID.get());

        register(TypePotionEffectType.get());
        register(TypeNamespacedKey.get());
        register(TypeEnchantment.get());

        register(TypePermission.get());
        register(TypeWorld.get());

        // Sender
        register(TypePlayer.get());
        register(TypeOfflinePlayer.get());
    }

}

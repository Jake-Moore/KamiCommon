package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.collections.KamiTreeSet;
import com.kamikazejam.kamicommon.util.comparator.ComparatorCaseInsensitive;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class KUtil {
    private static final Method methodGetOnlinePlayers;
    public final static String ID_PREFIX = "@";
    public final static String CONSOLE_ID = ID_PREFIX + "console";
    public final static Set<String> NOTHING_REMOVE = KUtil.treeset("", "none", "null", "nothing", "clear", "c", "delete", "del", "d", "erase", "e", "remove", "rem", "r", "reset", "res");

    static {
        methodGetOnlinePlayers = getMethodGetOnlinePlayers();
    }

    // -------------------------------------------- //
    // GET ONLINE PLAYERS
    // -------------------------------------------- //
    // It seems we can not always trust the Bukkit.getOnlinePlayers() method.
    // Due to compilation issue this method might not exist in the form we compiled against.
    // Spigot 1.8 and the 1.7 Bukkit might have been compiled slightly differently resulting in this issue.
    // Issue Example: https://github.com/MassiveCraft/MassiveCore/issues/192

    @SuppressWarnings("all")
    public static Method getMethodGetOnlinePlayers() {
        Method ret = null;
        try {
            for (Method method : Bukkit.class.getDeclaredMethods()) {
                // The method name must be getOnlinePlayers ...
                if (!method.getName().equals("getOnlinePlayers")) continue;

                // ... if we find such a method it's better than nothing ...
                if (ret == null) ret = method;

                // ... but if the method additionally returns a collection ...
                if (!method.getReturnType().isAssignableFrom(Collection.class)) continue;

                // ... that is preferable ...
                ret = method;

                // ... and we need not look any further.
                break;
            }
            ret.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ret;
    }


    // -------------------------------------------- //
    // EQUALS
    // -------------------------------------------- //
    @Contract(value = "null, null -> true; null, !null -> false; !null, null -> false", pure = true)
    public static boolean equals(Object object1, Object object2) {
        if (object1 == null) return object2 == null;
        if (object2 == null) return false;

        return object1.equals(object2);
    }

    @Contract("null -> fail")
    public static boolean equals(Object... objects) {
        if (objects == null) throw new NullPointerException("objects");
        if (objects.length % 2 != 0) throw new IllegalArgumentException("objects length not even");

        int index = 1;
        while (index < objects.length) {
            Object object1 = objects[index - 1];
            Object object2 = objects[index];

            if (!equals(object1, object2)) return false;

            index += 2;
        }

        return true;
    }

    // -------------------------------------------- //
    // COLOR CODE FROM HEX
    // -------------------------------------------- //

    public static ChatColor getNearestChatColor(String hex) {

        Color color = new Color(Integer.decode(hex));

        ChatColor nearest = null;
        Double distance = null;

        for (ChatColor chatColor : ChatColor.values()) {

            if (!chatColor.isColor()) continue;

            @Nullable Color checkColor = NmsAPI.getJavaColor(chatColor);
            if (checkColor == null) continue;
            int deltaR = color.getRed() - checkColor.getRed();
            int deltaG = color.getGreen() - checkColor.getGreen();
            int deltaB = color.getBlue() - checkColor.getBlue();

            double delta = Math.sqrt((deltaR * deltaR) + (deltaG * deltaG) + (deltaB * deltaB));
            if (distance == null || delta < distance) {
                nearest = chatColor;
                distance = delta;
            }
        }

        return nearest;
    }

    // -------------------------------------------- //
    // GET ONLINE SENDERS
    // -------------------------------------------- //
    // Used for retrieving the full set of senders currently present on this server.

    public static @NotNull ConsoleCommandSender getConsole() {
        return Bukkit.getConsoleSender();
    }

    public static @NotNull Set<CommandSender> getLocalSenders() {
        Set<CommandSender> ret = new KamiSet<>();

        // Add Online Players
        ret.addAll(KUtil.getOnlinePlayers());

        // Add Console
        ret.add(getConsole());

        return ret;
    }

    @SuppressWarnings("unchecked")
    public static Collection<Player> getOnlinePlayers() {
        // Fetch some kind of playersObject.
        Object playersObject = null;
        try {
            playersObject = Bukkit.getOnlinePlayers();
        } catch (Throwable t) {
            // That didn't work!
            // We probably just caught a NoSuchMethodError.
            // So let's try with reflection instead.
            try {
                playersObject = methodGetOnlinePlayers.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Now return the playersObject.
        if (playersObject instanceof Collection<?>) {
            return (Collection<Player>) playersObject;
        } else if (playersObject instanceof Player[]) {
            Player[] playersArray = (Player[]) playersObject;
            return Arrays.asList(playersArray);
        } else {
            throw new RuntimeException("Failed retrieving online players.");
        }
    }

    @Contract("null -> null")
    public static CommandSender getSender(Object senderObject) {
        // Null Return
        if (senderObject == null) return null;

        // Already Done
        if (senderObject instanceof CommandSender) return (CommandSender) senderObject;

        // Console Type
        // Handled at "Already Done"

        // Console Id/Name
        if (CONSOLE_ID.equals(senderObject)) return getConsole();

        // Player
        // Handled at "Already Done"

        // CommandSender
        // Handled at "Already Done"

        // OfflinePlayer
        if (senderObject instanceof OfflinePlayer) {
            return getSender(((OfflinePlayer) senderObject).getUniqueId());
        }

        // UUID
        if (senderObject instanceof UUID) {
            UUID uuid = (UUID) senderObject;
            // Attempt finding player
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) return player;
        }

        // String
        if (senderObject instanceof String) {
            String string = (String) senderObject;
            // Recurse as UUID
            UUID uuid = KUtil.asUuid(string);
            if (uuid != null) return getSender(uuid);

            // Bukkit API
            return Bukkit.getPlayerExact(string);
        }

        // Return Null
        return null;
    }


    // -------------------------------------------- //
    // UUID
    // -------------------------------------------- //

    @Contract("null -> null")
    public static UUID asUuid(String string) {
        // Null
        if (string == null) return null;

        // Avoid Exception
        if (string.length() != 36) return null;

        // Try
        try {
            return UUID.fromString(string);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isUuid(@Nullable String string) {
        return asUuid(string) != null;
    }


    // -------------------------------------------- //
    // SIMPLE CONSTRUCTORS
    // -------------------------------------------- //

    @SafeVarargs
    public static <T> @NotNull List<T> list(T @NotNull ... items) {
        List<T> ret = new KamiList<>(items.length);
        Collections.addAll(ret, items);
        return ret;
    }

    @SafeVarargs
    public static <T> @NotNull Set<T> set(T @NotNull ... items) {
        Set<T> ret = new KamiSet<>(items.length);
        Collections.addAll(ret, items);
        return ret;
    }

    @Contract("_ -> new")
    public static @NotNull Set<String> treeset(String @NotNull ... items) {
        return new KamiTreeSet<String, ComparatorCaseInsensitive>(ComparatorCaseInsensitive.get(), items);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> @NotNull Map<K, V> map(K key1, V value1, Object @NotNull ... objects) {
        Map<K, V> ret = new KamiMap<>();

        ret.put(key1, value1);

        Iterator<Object> iter = Arrays.asList(objects).iterator();
        while (iter.hasNext()) {
            K key = (K) iter.next();
            V value = (V) iter.next();
            ret.put(key, value);
        }

        return ret;
    }

    public static @NotNull List<Integer> range(int from, int to) {
        List<Integer> ret = new KamiList<>(to - from);
        for (int i = from; i < to; i++) {
            ret.add(i);
        }
        return ret;
    }

    // -------------------------------------------- //
    // IS(NT) NPC, SENDER, PLAYER
    // -------------------------------------------- //

    @Contract("null -> false")
    public static boolean isNpc(Object object) {
        if (!(object instanceof Metadatable)) return false;
        Metadatable metadatable = (Metadatable) object;
        try {
            return metadatable.hasMetadata("NPC");
        } catch (UnsupportedOperationException e) {
            // ProtocolLib - UnsupportedOperationException
            //   The method hasMetadata is not supported for temporary players. (ignore it)
            return false;
        }
    }

    @Contract("null -> true")
    public static boolean isntNpc(Object object) {
        return !isNpc(object);
    }

    @Contract("null -> false")
    public static boolean isSender(Object object) {
        if (!(object instanceof CommandSender)) return false;
        return !isNpc(object);
    }

    @Contract("null -> true")
    public static boolean isntSender(Object object) {
        return !isSender(object);
    }

    @Contract("null -> false")
    public static boolean isPlayer(Object object) {
        if (!(object instanceof Player)) return false;
        return !isNpc(object);
    }

    @Contract("null -> true")
    public static boolean isntPlayer(Object object) {
        return !isPlayer(object);
    }

    @Contract("null -> true")
    public static boolean isNothing(ItemStack itemStack) {
        if (itemStack == null) return true;
        if (itemStack.getAmount() == 0) return true;
        return itemStack.getType() == Material.AIR;
    }


    @Contract(pure = true)
    public static ChatColor getChatColor(@NotNull DyeColor dyeColor) {
        if (dyeColor.equals(DyeColor.WHITE)) {
            return ChatColor.WHITE;
        } else if (dyeColor.equals(DyeColor.ORANGE)) {
            return ChatColor.GOLD;
        } else if (dyeColor.equals(DyeColor.MAGENTA) || dyeColor.equals(DyeColor.PINK)) {
            return ChatColor.LIGHT_PURPLE;
        } else if (dyeColor.equals(DyeColor.LIGHT_BLUE)) {
            return ChatColor.AQUA;
        } else if (dyeColor.equals(DyeColor.YELLOW)) {
            return ChatColor.YELLOW;
        } else if (dyeColor.equals(DyeColor.LIME)) {
            return ChatColor.GREEN;
        } else if (dyeColor.equals(DyeColor.GRAY)) {
            return ChatColor.DARK_GRAY;
        } else if (dyeColor.name().equalsIgnoreCase("LIGHT_GRAY") || dyeColor.equals(DyeColor.BROWN)) {
            return ChatColor.GRAY;
        } else if (dyeColor.equals(DyeColor.CYAN)) {
            return ChatColor.DARK_AQUA;
        } else if (dyeColor.equals(DyeColor.PURPLE)) {
            return ChatColor.DARK_PURPLE;
        } else if (dyeColor.equals(DyeColor.BLUE)) {
            return ChatColor.BLUE;
        } else if (dyeColor.equals(DyeColor.GREEN)) {
            return ChatColor.DARK_GREEN;
        } else if (dyeColor.equals(DyeColor.RED)) {
            return ChatColor.RED;
        } else if (dyeColor.equals(DyeColor.BLACK)) {
            return ChatColor.BLACK;
        } else {
            throw new RuntimeException("Unknown DyeColor " + dyeColor);
        }
    }

    // -------------------------------------------- //
    // IS VALID PLAYER NAME
    // -------------------------------------------- //

    // The regex for a valid minecraft player name.
    public final static Pattern PATTERN_PLAYER_NAME = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

    public static boolean isValidPlayerName(String string) {
        return PATTERN_PLAYER_NAME.matcher(string).matches();
    }

    // -------------------------------------------- //
    // TP DELAY
    // -------------------------------------------- //
    // Teleportation delay permissions.

    public static final Map<String, Integer> permissionToTpdelay = KUtil.map(
            "kamicommon.notpdelay", 0,
            "default", 10
    );

    public static int getTpdelay(Permissible permissible) {
        Integer ret = pickFirstVal(permissible, permissionToTpdelay);
        if (ret == null) ret = 0;
        return ret;
    }

    @Contract("_, null -> null")
    public static <T> T pickFirstVal(@NotNull Permissible permissible, Map<String, T> perm2val) {
        if (perm2val == null) return null;
        T ret = null;

        for (Map.Entry<String, T> entry : perm2val.entrySet()) {
            ret = entry.getValue();
            if (hasPermission(permissible, entry.getKey())) break;
        }

        return ret;
    }

    @Contract("null, _ -> fail; !null, null -> fail")
    public static boolean hasPermission(Permissible permissible, String permission) {
        // Fail Fast
        if (permissible == null) throw new NullPointerException("permissible");
        if (permission == null) throw new NullPointerException("permission");
        return permissible.hasPermission(permission);
    }

    // -------------------------------------------- //
    // LOCATIONS COMPARISON
    // -------------------------------------------- //

    public static boolean isSameBlock(@NotNull PlayerMoveEvent event) {
        return isSameBlock(event.getFrom(), Objects.requireNonNull(event.getTo()));
    }

    public static boolean isSameBlock(@NotNull Location one, @NotNull Location two) {
        if (one.getBlockX() != two.getBlockX()) return false;
        if (one.getBlockZ() != two.getBlockZ()) return false;
        if (one.getBlockY() != two.getBlockY()) return false;
        return Objects.equals(one.getWorld(), two.getWorld());
    }

    public static boolean isSameChunk(@NotNull PlayerMoveEvent event) {
        return isSameChunk(event.getFrom(), Objects.requireNonNull(event.getTo()));
    }

    public static boolean isSameChunk(@NotNull Location one, @NotNull Location two) {
        if (one.getBlockX() >> 4 != two.getBlockX() >> 4) return false;
        if (one.getBlockZ() >> 4 != two.getBlockZ() >> 4) return false;
        return one.getWorld() == two.getWorld();
    }


    // -------------------------------------------- //
    // TRANSFORM
    // -------------------------------------------- //

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Predicate<? super T> where, Comparator<? super T> orderby, Integer limit, Integer offset) {
        // Collection
        Collection<T> collection = null;
        if (items instanceof Collection<?>) collection = (Collection<T>) items;

        // WHERE
        List<T> ret;
        if (where == null) {
            if (collection != null) {
                ret = new ArrayList<>(collection);
            } else {
                ret = new ArrayList<>();
                for (T item : items) {
                    ret.add(item);
                }
            }
        } else {
            if (collection != null) {
                ret = new ArrayList<>(collection.size());
            } else {
                ret = new ArrayList<>();
            }

            for (T item : items) {
                if (where.apply(item)) {
                    ret.add(item);
                }
            }
        }

        // ORDERBY
        if (orderby != null) {
            ret.sort(orderby);
        }

        // LIMIT AND OFFSET
        // Parse args
        int fromIndex = 0;
        if (offset != null) {
            fromIndex = offset;
        }

        int toIndex = ret.size() - 1;
        if (limit != null) {
            toIndex = fromIndex + limit;
        }

        // Clean args
        if (fromIndex <= 0) {
            fromIndex = 0;
        } else if (fromIndex > ret.size() - 1) {
            fromIndex = ret.size() - 1;
        }

        if (toIndex < fromIndex) {
            toIndex = fromIndex;
        } else if (toIndex > ret.size() - 1) {
            toIndex = ret.size() - 1;
        }

        // No limit?
        if (fromIndex == 0 && toIndex == ret.size() - 1) return ret;

        return new ArrayList<>(ret.subList(fromIndex, toIndex));
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Predicate<? super T> where) {
        return transform(items, where, null, null, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Predicate<? super T> where, Comparator<? super T> orderby) {
        return transform(items, where, orderby, null, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Predicate<? super T> where, Comparator<? super T> orderby, Integer limit) {
        return transform(items, where, orderby, limit, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Predicate<? super T> where, Integer limit) {
        return transform(items, where, null, limit, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Predicate<? super T> where, Integer limit, Integer offset) {
        return transform(items, where, null, limit, offset);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Comparator<? super T> orderby) {
        return transform(items, null, orderby, null, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Comparator<? super T> orderby, Integer limit) {
        return transform(items, null, orderby, limit, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Comparator<? super T> orderby, Integer limit, Integer offset) {
        return transform(items, null, orderby, limit, offset);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Integer limit) {
        return transform(items, null, null, limit, null);
    }

    public static <T> @NotNull List<T> transform(@NotNull Iterable<T> items, Integer limit, Integer offset) {
        return transform(items, null, null, limit, offset);
    }

    public static void printStackTrace() {
        try {
            throw new Exception();
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static void printStackTrace(String message) {
        try {
            throw new Exception(message);
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }




}

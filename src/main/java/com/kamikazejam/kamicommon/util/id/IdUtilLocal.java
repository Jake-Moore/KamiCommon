package com.kamikazejam.kamicommon.util.id;

import com.google.common.reflect.TypeToken;
import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.DiskUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Identification of a CommandSender can be done in 4 different ways.
 * <p>
 * 1. CommandSender sender (the sender itself)
 * 2. UUID senderUuid (the uuid for the sender)
 * 3. String senderId (the string id for the sender)
 * 4. String senderName (the name for the sender)
 * (5). Object senderObject (any of the four above)
 * <p>
 * This works very fine for players (instances of the Player class).
 * The UUID would be the Mojang account uuid, the id would be the stringified UUID and the name would be the current player name (it's always unique even though it can be changed).
 * <p>
 * Then there is the server console command sender (instance of ConsoleCommandSender).
 * This one does not natively have a proper uuid, id or name.
 * I have however given it some.
 * It's very often practical being able to treat the console as any player.
 * <p>
 * We provide the following features:
 * - Lookup of all the data based on one of the unique parts.
 * - Maintained sets for All, Online and Offline.
 * <p>
 * Players are registered automatically.
 * Console is registered with imaginary and deterministic data values.
 * Non standard CommandSenders must be manually registered using the register method.
 */
@SuppressWarnings({"SpellCheckingInspection", "unused", "ResultOfMethodCallIgnored"})
public class IdUtilLocal implements Listener, Runnable {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	// This class mostly contains static content.
	// A few things needs an instantiated class though.
	// Such as the event listeners.

	private static final IdUtilLocal i = new IdUtilLocal();
	public static IdUtilLocal get() { return i; }

	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	// Taken directly from my own imagination!

	public final static String IDPREFIX = "@";
	public final static String CONSOLE_ID = IDPREFIX + "console";
	public final static IdData CONSOLE_DATA = new IdData(CONSOLE_ID, CONSOLE_ID);

	// This is lock object is used when reading from and saving to this file.
	// Useful since saving might happen async.
	private final static Object CACHEFILE_LOCK = new Object();
	public final static File CACHEFILE = new File(KamiCommon.get().getDataFolder(), "idnamecache.json");
	public final static File CACHEFILE_TEMP = new File(KamiCommon.get().getDataFolder(), "idnamecache.json.temp");
	public final static Type CACHEFILE_TYPE = new TypeToken<Set<IdData>>() {
	}.getType();

	// -------------------------------------------- //
	// DATA STORAGE
	// -------------------------------------------- //
	// IdData storage. Maintaining relation between name and id.

	// The full set
	private static final Set<IdData> datas = Collections.newSetFromMap(new ConcurrentHashMap<>());

	@Contract(pure = true)
	public static Set<IdData> getDatas() {
		return datas;
	}

	// Id Index
	private static final Map<String, IdData> idToData = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

	@Contract(pure = true)
	public static Map<String, IdData> getIdToData() {
		return idToData;
	}

	// Name Index
	private static final Map<String, IdData> nameToData = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

	@Contract(pure = true)
	public static Map<String, IdData> getNameToData() {
		return nameToData;
	}

	// -------------------------------------------- //
	// MAINTAINED SETS
	// -------------------------------------------- //
	// Used for chat tab completion, argument readers, etc.

	private static final SenderMap maintainedIds = new SenderMap();

	@Contract(pure = true)
	public static SenderMap getMaintainedIds() {
		return maintainedIds;
	}

	public static @NotNull @UnmodifiableView Set<String> getIds(@NotNull SenderPresence presence, @NotNull SenderType type) {
		return maintainedIds.getValues(presence, type);
	}

	private static final SenderMap maintainedNames = new SenderMap();

	@Contract(pure = true)
	public static SenderMap getMaintainedNames() {
		return maintainedNames;
	}

	public static @NotNull @UnmodifiableView Set<String> getNames(@NotNull SenderPresence presence, @NotNull SenderType type) {
		return maintainedNames.getValues(presence, type);
	}

	// -------------------------------------------- //
	// REGISTRY
	// -------------------------------------------- //
	// For registering extra custom CommandSender implementations.
	// It's assumed that the getName() returns the name which is also the id.

	private static final Map<String, CommandSender> registryIdToSender = new ConcurrentHashMap<>();

	@Contract(value = " -> new", pure = true)
	public static @NotNull @UnmodifiableView Map<String, CommandSender> getRegistryIdToSender() {
		return Collections.unmodifiableMap(registryIdToSender);
	}

	private static final Map<CommandSender, String> registrySenderToId = new ConcurrentHashMap<>();

	@Contract(value = " -> new", pure = true)
	public static @NotNull @UnmodifiableView Map<CommandSender, String> getRegistrySenderToId() {
		return Collections.unmodifiableMap(registrySenderToId);
	}

	@Contract("null -> fail")
	public static void register(CommandSender sender) {
		if (sender == null) throw new NullPointerException("sender");
		String id = getId(sender);
		IdData data = new IdData(id, id);

		registryIdToSender.put(id, sender);
		registrySenderToId.put(sender, id);

		// Update data before the event is ran so that data is available.
		update(id, id, SenderPresence.LOCAL);
	}

	@Contract("null -> fail")
	public static void unregister(CommandSender sender) {
		if (sender == null) throw new NullPointerException("sender");
		String id = getId(sender);
		IdData data = new IdData(id, id);

		Object removed = registryIdToSender.remove(id);
		registrySenderToId.remove(sender);
		if (removed == null) return;

		// Update data before the event is ran so that data is available.
		update(id, id, SenderPresence.OFFLINE);
	}

	// -------------------------------------------- //
	// GET ONLINE SENDERS
	// -------------------------------------------- //
	// Used for retrieving the full set of senders currently present on this server.

	public static @NotNull Set<CommandSender> getLocalSenders() {
		Set<CommandSender> ret = new KamiSet<>();

		// Add Online Players
		ret.addAll(KUtil.getOnlinePlayers());

		// Add Console
		ret.add(getConsole());

		// Add Registered
		ret.addAll(registryIdToSender.values());

		return ret;
	}

	// -------------------------------------------- //
	// UPDATE ONE
	// -------------------------------------------- //

	// Returns the name corresponding to the removed id.
	@Contract("null -> fail")
	private static @Nullable String removeId(String id) {
		if (id == null) throw new NullPointerException("id");

		maintainedIds.removeValueCompletely(id);

		IdData data = idToData.remove(id);
		if (data == null) return null;
		datas.remove(data);
		return data.getName();
	}

	// Returns the id corresponding to the removed name.
	@Contract("null -> fail")
	private static @Nullable String removeName(String name) {
		if (name == null) throw new NullPointerException("name");

		maintainedNames.removeValueCompletely(name);

		IdData data = nameToData.remove(name);
		if (data == null) return null;
		datas.remove(data);
		return data.getId();
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	private static void addData(IdData data, SenderPresence presence) {
		if (data == null) throw new NullPointerException("data");
		if (presence == null) throw new NullPointerException("presence");

		String id = data.getId();
		String name = data.getName();

		datas.add(data);

		if (id != null) {
			idToData.put(id, data);
		}

		if (name != null) {
			nameToData.put(name, data);
		}

		if (id != null && name != null) {
			List<SenderPresence> presences = SenderMap.getPresences(presence);
			maintainedIds.addValue(id, presences);
			maintainedNames.addValue(name, presences);
		}
	}

	@Contract("null, null -> fail")
	public static void update(String id, String name) {
		update(id, name, null);
	}

	@Contract("null, null, _ -> fail")
	public static void update(String id, String name, long millis) {
		update(id, name, millis, null);
	}

	@Contract("null, null, _ -> fail")
	public static void update(final String id, final String name, SenderPresence presence) {
		update(id, name, System.currentTimeMillis(), presence);
	}

	public static void update(@NotNull IdData data) {
		update(data.getId(), data.getName(), data.getMillis(), null);
	}

	@Contract("null, null, _, _ -> fail")
	public static void update(final String id, final String name, final long millis, SenderPresence presence) {
		// First Null Check
		if (id == null && name == null)
			throw new NullPointerException("Either id or name must be set. They can't both be null.");

		// Calculate previous millis
		// The millis must be newer than the previous millis for the update to continue.
		Long previousMillis = null;
		Long idMillis = getMillis(id);
		if (idMillis != null) {
			previousMillis = idMillis;
		}
		Long nameMillis = getMillis(name);
		if (nameMillis != null && (previousMillis == null || nameMillis < previousMillis)) {
			previousMillis = nameMillis;
		}
		// The previousMillis is now null or the lowest of the two.
		if (previousMillis != null && previousMillis > millis) return;

		// Presence Fix
		if (presence == null) {
			if (id != null) {
				presence = maintainedIds.getPresence(id);
			} else {
				presence = maintainedNames.getPresence(name);
			}
		}

		// Removal of previous data
		removeIdAndNameRecurse(id, name);

		// Adding new data
		IdData data = new IdData(id, name, millis);
		addData(data, presence);
	}
	
	private static void removeIdAndNameRecurse(@Nullable String id, @Nullable String name) {
		String otherId = null;
		String otherName = null;

		// Remove first
		if (id != null) otherName = removeId(id);
		if (name != null) otherId = removeName(name);

		// If equal, then null. We shouldn't perform the same operation twice.
		if (otherId != null && otherId.equals(id)) otherId = null;
		if (otherName != null && otherName.equals(name)) otherName = null;

		// If any isn't null. Repeat
		if (otherId != null || otherName != null) {
			removeIdAndNameRecurse(otherId, otherName);
		}
	}

	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	// This is ran as the initial setup.
	// Do not call this manually.
	// It should only be called on plugin enable.

	// FIXME deal with this
	public static void setup() {
		// Time: Start
		long start = System.currentTimeMillis();

		// Clear Datas
		datas.clear();
		idToData.clear();
		nameToData.clear();

		maintainedIds.clear();
		maintainedNames.clear();


		// Load Datas
		loadDatas();

		// Since Console initially does not exist we schedule the register.
		Bukkit.getScheduler().scheduleSyncDelayedTask(KamiCommon.get(), () -> register(getConsole()));

		// Cachefile
		long ticks = 20 * 60; // 5min
		Bukkit.getScheduler().runTaskTimerAsynchronously(KamiCommon.get(), get(), ticks, ticks);

		// Register Event Listeners
		Bukkit.getPluginManager().registerEvents(get(), KamiCommon.get());

		// Time: End
		long end = System.currentTimeMillis();
		KamiCommon.get().getLogger().info(Txt.parse("<i>Setup of IdUtil took <h>%d<i>ms.", end - start));
	}

	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //
	// Here we maintain the maps continuously.
	// Filling them on setup only would not work.
	// When players log on/off that must be taken note of.

	// We don't care if it's cancelled or not.
	// We just wan't to make sure this id is known of and can be "fixed" asap.
	// Online or not? We just use the mixin to get the actuall value.

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerLoginLowest(@NotNull PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (KUtil.isntPlayer(player)) return;

		UUID uuid = player.getUniqueId();
		String id = uuid.toString();
		String name = player.getName();

		// Declaring Existence? Sure, whatever you were before!
		// It can definitely not be local at this point.
		// But online or offline is fine.
		boolean online = IdUtilLocal.isOnline(player);

		update(id, name, SenderPresence.fromOnline(online));
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerJoinLowest(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (KUtil.isntPlayer(player)) return;

		UUID uuid = player.getUniqueId();
		String id = uuid.toString();
		String name = player.getName();

		// Joining? The player must be local at this point.
		update(id, name, SenderPresence.LOCAL);
	}

	// Can't be cancelled
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerLeaveMonitor(@NotNull PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (KUtil.isntPlayer(player)) return;

		UUID uuid = player.getUniqueId();
		String id = uuid.toString();
		String name = player.getName();

		update(id, name, SenderPresence.OFFLINE);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerKickMonitor(@NotNull PlayerKickEvent event) {
		if (event.isCancelled()) { return; }

		Player player = event.getPlayer();
		if (KUtil.isntPlayer(player)) return;

		UUID uuid = player.getUniqueId();
		String id = uuid.toString();
		String name = player.getName();

		update(id, name, SenderPresence.OFFLINE);
	}


	// -------------------------------------------- //
	// GET DATA
	// -------------------------------------------- //
	// Get data from a senderObject (any of: CommandSender, UUID, String id, String name)
	// These methods avoid using the data cache if possible.

	@Contract("null -> null")
	public static IdData getData(Object senderObject) {
		// Null Return
		if (senderObject == null) return null;

		// Already Done
		if (senderObject instanceof IdData) return (IdData) senderObject;

		// Console Type
		if (senderObject instanceof ConsoleCommandSender) return CONSOLE_DATA;

		// Console Id/Name
		if (CONSOLE_ID.equals(senderObject)) return CONSOLE_DATA;

		// Player
		// CommandSender
		if (senderObject instanceof CommandSender) {
			String id = getIdFromSender((CommandSender) senderObject);
			return getIdToData().get(id);
		}

		// OfflinePlayer (UUID recurse)
		if (senderObject instanceof OfflinePlayer) {
			return getData(((OfflinePlayer) senderObject).getUniqueId());
		}

		// UUID
		if (senderObject instanceof UUID) {
			String id = getIdFromUuid((UUID) senderObject);
			return getIdToData().get(id);
		}

		// String
		if (senderObject instanceof String) {
			IdData ret = getIdToData().get(senderObject);
			if (ret != null) return ret;
			return getNameToData().get(senderObject);
		}

		// Return Null
		return null;
	}

	public static @NotNull ConsoleCommandSender getConsole() {
		return Bukkit.getConsoleSender();
	}

	@Contract("null -> null")
	public static Player getPlayer(Object senderObject) {
		return getAsPlayer(getSender(senderObject));
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

			// Otherwise assume registered sender
			return registryIdToSender.get(uuid.toString());
		}

		// String
		if (senderObject instanceof String) {
			String string = (String) senderObject;
			// Recurse as UUID
			UUID uuid = KUtil.asUuid(string);
			if (uuid != null) return getSender(uuid);

			// Registry
			CommandSender sender = registryIdToSender.get(string);
			if (sender != null) return sender;

			// Bukkit API
			return Bukkit.getPlayerExact(string);
		}

		// Return Null
		return null;
	}

	@Contract("null -> null")
	public static UUID getUuid(Object senderObject) {
		// Null Return
		if (senderObject == null) return null;

		// Already Done
		if (senderObject instanceof UUID) return (UUID) senderObject;

		// Console Type
		if (senderObject instanceof ConsoleCommandSender) return null;

		// Console Id/Name
		if (CONSOLE_ID.equals(senderObject)) return null;

		// Player
		if (senderObject instanceof Player) return ((Player) senderObject).getUniqueId();

		// CommandSender
		if (senderObject instanceof CommandSender) {
			CommandSender sender = (CommandSender) senderObject;
			String id = sender.getName();
			return KUtil.asUuid(id);
		}

		// OfflinePlayer
		if (senderObject instanceof OfflinePlayer) return ((OfflinePlayer) senderObject).getUniqueId();

		// UUID
		// Handled at "Already Done"

		// String
		if (senderObject instanceof String) {
			String string = (String) senderObject;
			// Is UUID
			UUID uuid = KUtil.asUuid(string);
			if (uuid != null) return uuid;

			// Is Name
			// Handled at "Data"
		}

		// Data
		IdData data = getData(senderObject);
		if (data != null) {
			String id = data.getId();
			if (id == null) return null;
			return KUtil.asUuid(id);
		}

		// Return Null
		return null;
	}

	// This method always returns null or a lower case String.
	@Contract("null -> null")
	public static String getId(Object senderObject) {
		// Null Return
		if (senderObject == null) return null;

		// Already Done
		if (senderObject instanceof String && KUtil.isUuid((String) senderObject)) return (String) senderObject;

		// Console
		// Handled at "Command Sender"

		// Console Id/Name
		if (CONSOLE_ID.equals(senderObject)) return CONSOLE_ID;

		// Player
		// Handled at "Command Sender"

		// Command Sender
		if (senderObject instanceof CommandSender) return getIdFromSender((CommandSender) senderObject);

		// OfflinePlayer
		// Handled at "Data"

		// UUID
		if (senderObject instanceof UUID) return getIdFromUuid((UUID) senderObject);

		// String
		// Handled at "Data"

		// Data
		IdData data = getData(senderObject);
		if (data != null) {
			return data.getId();
		}

		// Return Null
		return null;
	}

	@Contract("null -> !null")
	public static @Nullable String getIdFromSender(CommandSender sender) {
		if (sender instanceof Player) {
			try {
				return ((Player) sender).getUniqueId().toString();
			} catch (UnsupportedOperationException e) {
				// ProtocolLib: The method getUniqueId is not supported for temporary players.
				return null;
			}
		}
		if (sender instanceof ConsoleCommandSender) return CONSOLE_ID;

		// We blacklist all entities other than players.
		// This is because all entities are CommandSenders since Minecraft 1.9.
		// We do not want Arrows with id "arrow" in the database.
		if (sender instanceof Entity) return null;

		return sender.getName().toLowerCase();
	}

	@Contract(pure = true)
	public static String getIdFromUuid(@NotNull UUID uuid) {
		return uuid.toString();
	}

	@Contract("null -> null")
	public static String getName(Object senderObject) {
		// Null Return
		if (senderObject == null) return null;

		// Already Done
		// Handled at "Data" (not applicable - names can look differently)

		// Console
		// Handled at "Command Sender"

		// Console Id/Name
		if (CONSOLE_ID.equals(senderObject)) return CONSOLE_ID;

		// Player
		// Handled at "Command Sender"

		// CommandSender
		if (senderObject instanceof CommandSender) return getNameFromSender((CommandSender) senderObject);

		// UUID
		// Handled at "Data".

		// String
		// Handled at "Data"
		// Note: We try to use stored data to fix the capitalization!

		// Data
		IdData data = getData(senderObject);
		if (data != null) {
			return data.getName();
		}

		// TryFix Behavior
		// Note: We try to use stored data to fix the capitalization!
		if (senderObject instanceof String) return (String) senderObject;

		// Return Null
		return null;
	}

	@Contract("null -> null")
	public static OfflinePlayer getOfflinePlayer(Object senderObject) {
		// Null Return
		if (senderObject == null) return null;

		// Already done
		if (senderObject instanceof OfflinePlayer) return (OfflinePlayer) senderObject;

		//
		UUID uuid = getUuid(senderObject);
		if (uuid == null) return null;

		return Bukkit.getOfflinePlayer(uuid);
	}

	public static String getNameFromSender(@NotNull CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) return CONSOLE_ID;
		return sender.getName();
	}

	@Contract("null -> false")
	public static boolean isOnline(Object senderObject) {
		// Fix the id ...
		String id = getId(senderObject);
		if (id == null) return false;

		// ... console is always online ...
		// (this counters console being offline till the first tick due to deferred register)
		if (CONSOLE_ID.equals(id)) return true;

		// ... return by (case insensitive) set contains.
		return IdUtilLocal.getIds(SenderPresence.ONLINE, SenderType.ANY).contains(id);
	}

	@Contract("null -> null")
	public static Long getMillis(Object senderObject) {
		IdData data = getData(senderObject);
		if (data == null) return null;
		return data.getMillis();
	}

	// -------------------------------------------- //
	// ID TYPE CHECKING
	// -------------------------------------------- //

	public static boolean isPlayerId(String string) {
		// NOTE: Assuming all custom ids isn't a valid player name or id.
		return KUtil.isValidPlayerName(string) || KUtil.isUuid(string);
	}

	@Contract("null -> false")
	public static boolean isPlayer(Object senderObject) {
		String id = IdUtilLocal.getId(senderObject);
		if (id == null) return false;
		return isPlayerId(id);
	}

	@Contract(value = "null -> false", pure = true)
	public static boolean isConsoleId(String string) {
		return CONSOLE_ID.equals(string);
	}

	@Contract("null -> false")
	public static boolean isConsole(Object senderObject) {
		String id = IdUtilLocal.getId(senderObject);
		if (id == null) return false;
		return isConsoleId(id);
	}

	// -------------------------------------------- //
	// GET AS
	// -------------------------------------------- //

	@Contract(value = "null -> null", pure = true)
	public static CommandSender getAsSender(Object object) {
		if (!(object instanceof CommandSender)) return null;
		return (CommandSender) object;
	}

	@Contract(value = "null -> null", pure = true)
	public static Player getAsPlayer(Object object) {
		if (!(object instanceof Player)) return null;
		return (Player) object;
	}

	@Contract(value = "null -> null", pure = true)
	public static ConsoleCommandSender getAsConsole(Object object) {
		if (!(object instanceof ConsoleCommandSender)) return null;
		return (ConsoleCommandSender) object;
	}

	// -------------------------------------------- //
	// CONVENIENCE GAME-MODE
	// -------------------------------------------- //

	@Contract("null, _ -> param2")
	public static GameMode getGameMode(@Nullable Object object, GameMode def) {
		Player player = getPlayer(object);
		if (player == null) return def;
		return player.getGameMode();
	}

	@Contract("null, _, _ -> param3")
	public static boolean isGameMode(@Nullable Object object, GameMode gm, boolean def) {
		Player player = getPlayer(object);
		if (player == null) return def;
		return player.getGameMode() == gm;
	}

	// -------------------------------------------- //
	// DATAS
	// -------------------------------------------- //

	public static void loadDatas() {
		KamiCommon.get().getLogger().info(Txt.parse("<i>Loading Cachefile datas..."));
		for (IdData data : getCachefileDatas()) {
			update(data.getId(), data.getName(), data.getMillis(), SenderPresence.OFFLINE);
		}

		KamiCommon.get().getLogger().info(Txt.parse("<i>Loading Onlineplayer datas..."));
		for (IdData data : getLocalPlayerDatas()) {
			update(data.getId(), data.getName(), data.getMillis(), SenderPresence.LOCAL);
		}

		KamiCommon.get().getLogger().info(Txt.parse("<i>Loading Registry datas..."));
		for (String id : registryIdToSender.keySet()) {
			update(id, id, SenderPresence.LOCAL);
		}

		KamiCommon.get().getLogger().info(Txt.parse("<i>Saving Cachefile..."));
		saveCachefileDatas();
	}

	// -------------------------------------------- //
	// CACHEFILE DATAS
	// -------------------------------------------- //

	public static void saveCachefileDatas() {
		synchronized (CACHEFILE_LOCK) {
			String content = KamiCommon.gson.toJson(datas, CACHEFILE_TYPE);
			DiskUtil.writeCatch(CACHEFILE_TEMP, content);
			if (!CACHEFILE_TEMP.exists()) return;
			CACHEFILE.delete();
			CACHEFILE_TEMP.renameTo(CACHEFILE);
		}
	}

	public static Set<IdData> getCachefileDatas() {
		synchronized (CACHEFILE_LOCK) {
			String content = DiskUtil.readCatch(CACHEFILE);

			if (content == null) return new HashSet<>();
			content = content.trim();
			if (content.isEmpty()) return new HashSet<>();

			return KamiCommon.gson.fromJson(content, CACHEFILE_TYPE);
		}
	}

	@Override
	public void run() {
		saveCachefileDatas();
	}

	// -------------------------------------------- //
	// ONLINEPLAYER DATAS
	// -------------------------------------------- //
	// This data source is simply based on the players currently online

	public static @NotNull Set<@NotNull IdData> getLocalPlayerDatas() {
		Set<IdData> ret = new KamiSet<>();

		long millis = System.currentTimeMillis();

		for (Player player : KUtil.getOnlinePlayers()) {
			String id = getId(player);
			if (id == null) throw new NullPointerException("id");

			String name = getName(player);
			if (name == null) throw new NullPointerException("name");

			IdData data = new IdData(id, name, millis);

			ret.add(data);
		}

		return ret;
	}

}

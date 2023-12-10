package com.kamikazejam.kamicommon.util.id;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@Getter
public final class SenderMap {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final Map<SenderPresence, Map<SenderType, Set<String>>> innerMap;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public SenderMap() {
		innerMap = new EnumMap<>(SenderPresence.class);
		for (SenderPresence presence : SenderPresence.values()) {
			Map<SenderType, Set<String>> map = new EnumMap<>(SenderType.class);
			for (SenderType type : SenderType.values()) {
				Set<String> set = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
				map.put(type, set);
			}
			innerMap.put(presence, map);
		}
	}

	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //

	@Contract("null, _ -> fail; !null, null -> fail")
	public @NotNull @UnmodifiableView Set<String> getValues(SenderPresence presence, SenderType type) {
		if (presence == null) throw new NullPointerException("presence");
		if (type == null) throw new NullPointerException("type");

		return Collections.unmodifiableSet(getRawValues(presence, type));
	}

	private Set<String> getRawValues(SenderPresence presence, SenderType type) {
		return innerMap.get(presence).get(type);
	}

	@Contract("null -> fail")
	public SenderPresence getPresence(String value) {
		if (value == null) throw new NullPointerException("value");
		return getPresence(value, SenderType.ANY);
	}

	public SenderPresence getPresence(String value, SenderType type) {
		if (value == null) throw new NullPointerException("value");
		if (type == null) throw new NullPointerException("type");


		for (SenderPresence presence : SenderPresence.values()) {
			if (contains(value, presence, type)) return presence;
		}

		return null;
	}

	// -------------------------------------------- //
	// CONTAINS
	// -------------------------------------------- //

	@Contract("null, _, _ -> fail; !null, null, _ -> fail; !null, !null, null -> fail")
	public boolean contains(String value, SenderPresence presence, SenderType type) {
		if (value == null) throw new NullPointerException("value");
		if (presence == null) throw new NullPointerException("presence");
		if (type == null) throw new NullPointerException("type");

		return getRawValues(presence, type).contains(value);
	}

	// -------------------------------------------- //
	// CLEAR
	// -------------------------------------------- //

	public void clear() {
		for (Map<SenderType, Set<String>> map : innerMap.values()) {
			for (Set<String> set : map.values()) {
				set.clear();
			}
		}
	}

	// -------------------------------------------- //
	// ADD
	// -------------------------------------------- //

	@Contract("null, _ -> fail; !null, null -> fail")
	public void addValue(String value, SenderPresence presence) {
		if (value == null) throw new NullPointerException("value");
		if (presence == null) throw new NullPointerException("presence");

		addValue(value, getPresences(presence));
	}

	@Contract("null, _ -> fail; !null, null -> fail")
	public void addValue(String value, List<SenderPresence> presences) {
		if (value == null) throw new NullPointerException("value");
		if (presences == null) throw new NullPointerException("presences");

		addValue(value, presences, getSenderTypes(value));
	}

	@Contract("null, _, _ -> fail; !null, null, _ -> fail; !null, !null, null -> fail")
	public void addValue(String value, List<SenderPresence> presences, List<SenderType> types) {
		if (value == null) throw new NullPointerException("value");
		if (presences == null) throw new NullPointerException("presences");
		if (types == null) throw new NullPointerException("types");

		for (SenderPresence presence : presences) {
			Map<SenderType, Set<String>> map = innerMap.get(presence);
			for (SenderType type : types) {
				Set<String> set = map.get(type);
				set.add(value);
			}
		}
	}

	// -------------------------------------------- //
	// REMOVE
	// -------------------------------------------- //

	@Contract("null -> fail")
	public boolean removeValueCompletely(String value) {
		if (value == null) throw new NullPointerException("value");

		boolean ret = false;
		for (Map<SenderType, Set<String>> map : innerMap.values()) {
			for (Set<String> set : map.values()) {
				ret |= set.remove(value);
			}
		}
		return ret;
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	public static final List<SenderPresence> LOCAL_PRESENCES = ImmutableList.of(SenderPresence.LOCAL, SenderPresence.ONLINE, SenderPresence.ANY);
	public static final List<SenderPresence> ONLINE_PRESENCES = ImmutableList.of(SenderPresence.ONLINE, SenderPresence.ANY);
	public static final List<SenderPresence> OFFLINE_PRESENCES = ImmutableList.of(SenderPresence.OFFLINE, SenderPresence.ANY);

	// This accepts the most strict presence,
	// and returns all other which also match.
	@Contract("null -> fail")
	public static @NotNull List<@NotNull SenderPresence> getPresences(SenderPresence presence) {
		if (presence == null) throw new NullPointerException("presence");

		if (presence == SenderPresence.LOCAL) return LOCAL_PRESENCES;
		else if (presence == SenderPresence.ONLINE) return ONLINE_PRESENCES;
		else if (presence == SenderPresence.OFFLINE) return OFFLINE_PRESENCES;
		else throw new IllegalArgumentException("SenderPresence.ANY is not supported. You must know if it is online or offline.");
	}

	public static final List<SenderType> PLAYER_TYPES = ImmutableList.of(SenderType.PLAYER, SenderType.ANY);
	public static final List<SenderType> NONPLAYER_TYPES = ImmutableList.of(SenderType.NONPLAYER, SenderType.ANY);

	@Contract("null -> fail")
	public static @NotNull List<@NotNull SenderType> getSenderTypes(String value) {
		if (value == null) throw new NullPointerException("value");
		if (isPlayerValue(value)) return PLAYER_TYPES;
		else return NONPLAYER_TYPES;
	}

	public static boolean isPlayerValue(String value) {
		return IdUtilLocal.isPlayerId(value);
		//return MUtil.isValidPlayerName(value) || MUtil.isUuid(value);
	}

}

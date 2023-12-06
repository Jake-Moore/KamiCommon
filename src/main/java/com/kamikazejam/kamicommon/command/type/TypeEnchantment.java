package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.nms.NmsManager;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import com.kamikazejam.kamicommon.util.nms.NMS_Methods;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class TypeEnchantment extends TypeAbstractChoice<Enchantment> {
	// -------------------------------------------- //
	// DATA
	// -------------------------------------------- //
	// http://minecraft.gamepedia.com/Enchanting#Enchantments

	// The first name is taken from the wiki. Those names are those people think of.
	// The second name is the Spigot key name.
	// Thereafter comes assorted extras
	public static Map<String, List<String>> ID_TO_RAWNAMES = new KamiMap<>(
			"protection", new KamiList<>("Protection", "PROTECTION_ENVIRONMENTAL"),
			"fire_protection", new KamiList<>("Fire Protection", "PROTECTION_FIRE"),
			"feather_falling", new KamiList<>("Feather Falling", "PROTECTION_FALL", "FallProtection"),
			"blast_protection", new KamiList<>("Blast Protection", "PROTECTION_EXPLOSIONS", "ExplosionProtection"),
			"projectile_protection", new KamiList<>("Projectile Protection", "PROTECTION_PROJECTILE", "ProjectileProtection"),
			"respiration", new KamiList<>("Respiration", "OXYGEN", "Breathing"),
			"aqua_affinity", new KamiList<>("Aqua Affinity", "WATER_WORKER"),
			"thorns", new KamiList<>("Thorns", "THORNS"),
			"depth_strider", new KamiList<>("Depth Strider", "DEPTH_STRIDER"),
			"frost_walker", new KamiList<>("Frost Walker", "FROST_WALKER"),
			"binding_curse", new KamiList<>("Curse of Binding", "BINDING_CURSE", "BindingCurse"),
			"sharpness", new KamiList<>("Sharpness", "DAMAGE_ALL"),
			"smite", new KamiList<>("Smite", "DAMAGE_UNDEAD"),
			"bane_of_arthropods", new KamiList<>("Bane of Arthropods", "DAMAGE_ARTHROPODS", "BaneArthropods", "Arthropods"),
			"knockback", new KamiList<>("Knockback", "KNOCKBACK"),
			"fire_aspect", new KamiList<>("Fire Aspect", "FIRE_ASPECT"),
			"looting", new KamiList<>("Looting", "LOOT_BONUS_MOBS"),
			"sweeping", new KamiList<>("Sweeping Edge", "SWEEPING_EDGE"),
			"efficiency", new KamiList<>("Efficiency", "DIG_SPEED"),
			"silk_touch", new KamiList<>("Silk Touch", "SILK_TOUCH"),
			"unbreaking", new KamiList<>("Unbreaking", "DURABILITY"),
			"fortune", new KamiList<>("Fortune", "LOOT_BONUS_BLOCKS"),
			"power", new KamiList<>("Power", "ARROW_DAMAGE"),
			"punch", new KamiList<>("Punch", "ARROW_KNOCKBACK"),
			"flame", new KamiList<>("Flame", "ARROW_FIRE"),
			"infinity", new KamiList<>("Infinity", "ARROW_INFINITE", "ArrowInfinity"),
			"luck_of_the_sea", new KamiList<>("Luck of the Sea", "LUCK", "LuckOfSea", "LuckTheSea", "LuckSea"),
			"lure", new KamiList<>("Lure", "LURE"),
			"mending", new KamiList<>("Mending", "MENDING"),
			"vanishing_curse", new KamiList<>("Curse of Vanishing", "VANISHING_CURSE"),
			"loyalty", new KamiList<>("Loyalty", "LOYALTY"),
			"impaling", new KamiList<>("Impaling", "IMPALING"),
			"riptide", new KamiList<>("Riptide", "RIPTIDE"),
			"channeling", new KamiList<>("Channeling", "CHANNELING"),
			"piercing", new KamiList<>("Piercing", "PIERCING"),
			"multishot", new KamiList<>("Multishot", "MULTISHOT"),
			"quick_charge", new KamiList<>("Quick Charge", "QUICK_CHARGE", "QuickCharge"),
			"soul_speed", new KamiList<>("Soul Speed", "SOUL_SPEED", "SoulSpeed"),
			"swift_sneak", new KamiList<>("Swift Sneak", "SWIFT_SNEAK", "SwiftSneak")
	);

	public static @NotNull String enchantmentToKey(@NotNull Enchantment enchantment) {
		return NMS_Methods.getNamespaced(enchantment);
	}

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeEnchantment i = new TypeEnchantment();

	public static TypeEnchantment get() {
		return i;
	}

	private TypeEnchantment() {
		super(Enchantment.class);
		this.setVisualColor(ChatColor.AQUA);
		this.setAll(Enchantment.values());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getNameInner(Enchantment enchantment) {
		String rawname = enchantmentToKey(enchantment);
		List<String> rawnames = ID_TO_RAWNAMES.get(enchantmentToKey(enchantment));
		if (rawnames != null) rawname = rawnames.get(0);
		return Txt.getNicedEnumString(rawname);
	}

	@Override
	public Set<String> getNamesInner(Enchantment enchantment) {
		// Create
		Set<String> ret = new KamiSet<>();

		// Fill
		List<String> raws = new KamiList<>();
		List<String> rawNames = ID_TO_RAWNAMES.get(enchantmentToKey(enchantment));
		if (rawNames != null) raws.addAll(rawNames);

		// If after 1.13, we have access to namespaced keys
		if (NmsManager.getFormattedNmsDouble() >= 1130) {
			raws.add(enchantmentToKey(enchantment));
		}

		for (String raw : raws) {
			ret.add(Txt.getNicedEnumString(raw));
		}

		// Return
		return ret;
	}

	@Override
	public String getIdInner(Enchantment enchantment) {
		return enchantmentToKey(enchantment);
	}

}

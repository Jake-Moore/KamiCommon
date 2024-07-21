package com.kamikazejam.kamicommon.command.type;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class TypePotionEffectType extends TypeAbstractChoice<PotionEffectType> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypePotionEffectType i = new TypePotionEffectType();

	public static TypePotionEffectType get() {
		return i;
	}

	public TypePotionEffectType() {
		super(PotionEffectType.class);
		this.setAll(PotionEffectType.values());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //


	@Override
	public @Nullable String getName(@Nullable PotionEffectType value) {
		if (value == null) return null;
		return value.getName();
	}

	@Override
	public @Nullable String getId(@Nullable PotionEffectType value) {
		if (value == null) return null;
		return value.getName();
	}

}

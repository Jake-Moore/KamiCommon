package com.kamikazejam.kamicommon.command.type;

import org.bukkit.potion.PotionEffectType;

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
	public String getNameInner(PotionEffectType value) {
		return value.getName();
	}

	@Override
	public String getIdInner(PotionEffectType value) {
		return value.getName();
	}

}

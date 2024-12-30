package com.kamikazejam.kamicommon.command.type.primitive;

import com.kamikazejam.kamicommon.command.type.TypeAbstractChoice;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public abstract class TypeBooleanAbstract extends TypeAbstractChoice<Boolean> {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public static final String NAME_YES = "Yes";
	public static final String NAME_TRUE = "True";
	public static final String NAME_ON = "On";

	public static final String NAME_NO = "No";
	public static final String NAME_FALSE = "False";
	public static final String NAME_OFF = "Off";

	public static final Set<String> NAMES_TRUE = new KamiSet<>(
			NAME_YES,
			NAME_TRUE,
			NAME_ON
	);

	public static final Set<String> NAMES_FALSE = new KamiSet<>(
			NAME_NO,
			NAME_FALSE,
			NAME_OFF
	);

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected final String stringTrue;

	public String getNameTrue() {
		return this.stringTrue;
	}

	protected final String stringFalse;

	public String getNameFalse() {
		return this.stringFalse;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public TypeBooleanAbstract(String t, String f) {
		super(Boolean.class);
		this.stringTrue = t;
		this.stringFalse = f;
		this.setAll(
				Boolean.TRUE,
				Boolean.FALSE
		);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public @NotNull String getName() {
		return "toggle";
	}

	@Override
	public @Nullable String getName(@Nullable Boolean value) {
		if (value == null) return null;
		return value ? this.getNameTrue() : this.getNameFalse();
	}

	@Override
	public @NotNull Set<String> getNames(@Nullable Boolean value) {
		if (value == null) return Collections.emptySet();

		// Create
		Set<String> ret = new KamiSet<>();

		// Fill
		ret.add(this.getName(value));
		ret.addAll(value ? NAMES_TRUE : NAMES_FALSE);

		// Return
		return ret;
	}

	@Override
	public @Nullable String getId(@Nullable Boolean value) {
		if (value == null) return null;
		return value.toString();
	}

}

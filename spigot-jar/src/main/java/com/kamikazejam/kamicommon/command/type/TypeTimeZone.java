package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.util.KUtil;

import java.util.Collection;
import java.util.TimeZone;

public class TypeTimeZone extends TypeAbstractChoice<String> {
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //

	private static final TypeTimeZone i = new TypeTimeZone();

	public static TypeTimeZone get() {
		return i;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public TypeTimeZone() {
		super(String.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Collection<String> getAll() {
		return KUtil.list(TimeZone.getAvailableIDs());
	}
}

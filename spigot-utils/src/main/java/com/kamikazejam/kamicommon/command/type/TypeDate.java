package com.kamikazejam.kamicommon.command.type;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class TypeDate extends TypeAbstractSimple<Date> {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeDate i = new TypeDate();

	public static TypeDate get() {
		return i;
	}

	public TypeDate() {
		super(Date.class);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public @NotNull String getName() {
		return "YYYY-MM-DD date";
	}

	@Override
	public String getId(Date value) {
		return DATE_FORMAT.format(value);
	}

	@Override
	public Date valueOf(String arg, CommandSender sender) throws Exception {
		return DATE_FORMAT.parse(arg);
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return Collections.emptySet();
	}

}

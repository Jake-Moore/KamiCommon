package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.type.sender.TypeSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropertyUsed<V> extends Property<CommandSender, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public PropertyUsed(@NotNull EditSettings<V> settings, @Nullable V used) {
		super(TypeSender.get(), settings.getObjectType(), "used " + settings.getObjectType().getName());
		this.addRequirements(settings.getUsedRequirements());
		this.used = used;
	}

	public PropertyUsed(@NotNull EditSettings<V> settings) {
		this(settings, null);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	// We provide a default implementation with "the used" stored internally.
	// This makes sense for a few cases.
	// Such as when we edit the same instance all the time, such as a configuration.
	// Most of the time these methods will however be overridden.

	private V used;

	@Override
	public V getRaw(CommandSender sender) {
		return this.used;
	}

	@Override
	public CommandSender setRaw(CommandSender sender, V used) {
		this.used = used;
		return sender;
	}

}

package com.kamikazejam.kamicommon.command.type.container;

import org.bukkit.command.CommandSender;

import java.util.Collection;

public interface AllAble<T> {
	Collection<T> getAll(CommandSender sender);
}

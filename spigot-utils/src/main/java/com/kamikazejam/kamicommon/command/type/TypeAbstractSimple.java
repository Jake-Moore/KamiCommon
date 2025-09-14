package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class TypeAbstractSimple<T> extends TypeAbstractException<T> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public TypeAbstractSimple(Class<? extends T> clazz) {
		super(clazz);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

    @Override
    public @NotNull VersionedComponent extractErrorMessageMini(String arg, CommandSender sender, Exception ex) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();

        String error = KamiCommand.Config.getErrorColorMini();
        String param = KamiCommand.Config.getErrorParamColorMini();
        String miniMessage = String.format(error + "\"" + param + "%s" + error + "\" is not a %s.", arg, this.getName());
        return serializer.fromMiniMessage(miniMessage);
    }

}

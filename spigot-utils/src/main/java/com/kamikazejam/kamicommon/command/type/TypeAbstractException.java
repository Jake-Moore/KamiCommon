package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.serializer.VersionedComponentSerializer;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Type} base for parsing that signals failure by throwing. Implement
 * {@link #valueOf(String, CommandSender)} and let it throw anything on bad input;
 * {@link #read(String, CommandSender)} catches that and rethrows a {@link KamiCommonException} carrying
 * {@link #extractErrorMessageMini(String, CommandSender, Exception)}, which defaults to the thrown
 * exception's own message and is meant to be overridden for anything a player will read.
 */
public abstract class TypeAbstractException<T> extends TypeAbstract<T> {
    // -------------------------------------------- //
    // ABSTRACT
    // -------------------------------------------- //

    public abstract T valueOf(String arg, CommandSender sender) throws Exception;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public TypeAbstractException(Class<?> clazz) {
        super(clazz);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public T read(String arg, CommandSender sender) throws KamiCommonException {
        try {
            return this.valueOf(arg, sender);
        } catch (Exception ex) {
            throw new KamiCommonException().addMsg(this.extractErrorMessageMini(arg, sender, ex));
        }
    }

    // -------------------------------------------- //
    // MESSAGE (OVERRIDABLE)
    // -------------------------------------------- //

    @NotNull
    public VersionedComponent extractErrorMessageMini(String arg, CommandSender sender, Exception ex) {
        VersionedComponentSerializer serializer = NmsAPI.getVersionedComponentSerializer();
        return serializer.fromMiniMessage(KamiCommand.Config.getErrorColorMini())
                .append(serializer.fromPlainText(ex.getMessage()));
    }

}

package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;

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
            throw new KamiCommonException().addMsg(this.extractErrorMessage(arg, sender, ex));
        }
    }

    // -------------------------------------------- //
    // MESSAGE (OVERRIDABLE)
    // -------------------------------------------- //

    public String extractErrorMessage(String arg, CommandSender sender, Exception ex) {
        return KamiCommand.Config.getErrorColor() + ex.getMessage();
    }

}

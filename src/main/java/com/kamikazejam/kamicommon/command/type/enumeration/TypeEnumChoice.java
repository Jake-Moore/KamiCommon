package com.kamikazejam.kamicommon.command.type.enumeration;

import com.kamikazejam.kamicommon.command.type.TypeAbstractChoice;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class TypeEnumChoice<T extends Enum<T>> extends TypeAbstractChoice<T> {

    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //
    private final Class<T> enumClass;
    public TypeEnumChoice(Class<T> enumClass) {
        super(enumClass);
        this.enumClass = enumClass;
        this.setAll(enumClass.getEnumConstants());
    }

    // -------------------------------------------- //
    // READ
    // -------------------------------------------- //

    @Override
    public T read(String arg, CommandSender sender) throws KamiCommonException {
        try {
            return Enum.valueOf(enumClass, arg.toUpperCase());
        }catch (IllegalArgumentException | NullPointerException e) {
            String[] parts = enumClass.getSimpleName().split("\\.");
            String name = parts[parts.length - 1].toLowerCase()
                    .replace("_", " ")
                    .replace("[]", "");

            throw new KamiCommonException().addMsg("<b>No %s matches \"<h>%s<b>\".", name, arg);
        }
    }

    @Override
    public Collection<String> getTabList(CommandSender sender, String arg) {
        return this.getAll().stream().map(g -> g.name().toLowerCase()).collect(Collectors.toList());
    }
}

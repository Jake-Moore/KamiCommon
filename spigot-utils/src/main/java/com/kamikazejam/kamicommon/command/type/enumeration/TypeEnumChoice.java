package com.kamikazejam.kamicommon.command.type.enumeration;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.type.TypeAbstract;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TypeEnumChoice<T extends Enum<T>> extends TypeAbstract<T> {

    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //
    private final Class<T> enumClass;
    private final List<T> exclusions;
    private final String enumName;
    private final List<T> allowed;
    @SafeVarargs
    public TypeEnumChoice(Class<T> enumClass, T... exclusions) {
        super(enumClass);
        this.enumClass = enumClass;
        this.exclusions = Arrays.asList(exclusions);

        allowed = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
        allowed.removeAll(this.exclusions);

        String[] parts = enumClass.getSimpleName().split("\\.");
        this.enumName = parts[parts.length - 1].toLowerCase()
                .replace("_", " ")
                .replace("[]", "");
    }

    // -------------------------------------------- //
    // READ
    // -------------------------------------------- //

    @Override
    public T read(String arg, CommandSender sender) throws KamiCommonException {
        try {
            // Make sure to process exclusions
            T t = Enum.valueOf(enumClass, arg.toUpperCase());
            if (exclusions.contains(t)) {
                throw new IllegalArgumentException("Excluded enum");
            }

            return t;
        }catch (IllegalArgumentException | NullPointerException e) {
            String error = KamiCommand.Config.getErrorColorMini();
            String param = KamiCommand.Config.getErrorParamColorMini();
            throw new KamiCommonException().addMsgFromMiniMessage(
                    error + "No %s matches \"" + param + "%s" + error + "\".",
                    enumName, arg
            );
        }
    }

    @Override
    public Collection<String> getTabList(CommandSender sender, String arg) {
        // getAll already processed exclusions
        return this.allowed.stream()
                .map(e -> e.name().toLowerCase())
                .filter(e -> e.startsWith(arg))
                .limit(20)
                .collect(Collectors.toList());
    }
}

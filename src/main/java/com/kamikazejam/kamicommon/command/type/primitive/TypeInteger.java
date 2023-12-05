package com.kamikazejam.kamicommon.command.type.primitive;

import org.bukkit.command.CommandSender;

public class TypeInteger extends TypeAbstractNumber<Integer> {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final TypeInteger i = new TypeInteger();

    public static TypeInteger get() {
        return i;
    }

    public TypeInteger() {
        super(Integer.class);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public String getName() {
        return "number";
    }

    @Override
    public Integer valueOf(String arg, CommandSender sender) throws Exception {
        return Integer.parseInt(arg);
    }

}
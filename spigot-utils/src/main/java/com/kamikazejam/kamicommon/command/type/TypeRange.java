package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@SuppressWarnings("unused")
public class TypeRange extends TypeInteger {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	protected TypeRange(int MIN_RANGE, int MAX_RANGE) {
		this.MIN_RANGE = MIN_RANGE;
		this.MAX_RANGE = MAX_RANGE;
	}

	@Contract("_, _ -> new")
	public static @NotNull TypeRange get(int min, int max) {
		return new TypeRange(min, max);
	}

	@Contract("_ -> new")
	public static @NotNull TypeRange get(@Range(from = 1, to = Integer.MAX_VALUE) int max) {
		return new TypeRange(0, max);
	}

	// -------------------------------------------- //
	// CONSTANT
	// -------------------------------------------- //

	public final int MIN_RANGE;
	public final int MAX_RANGE;

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Integer read(String arg, CommandSender sender) throws KamiCommonException {
		Integer ret = super.read(arg, sender);

		if (ret <= MIN_RANGE || ret > MAX_RANGE) {
            String error = KamiCommand.Config.getErrorColorMini();
            String param = KamiCommand.Config.getErrorParamColorMini();
            throw new KamiCommonException().addMsgFromMiniMessage(
                    error + "Invalid range " + param + "%d." + error + " Range must be between %d and %d.",
                    ret, MIN_RANGE, MAX_RANGE
            );
        }

		return ret;
	}

}

package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TypeColor extends TypeAbstract<Color> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeColor i = new TypeColor();

	public static TypeColor get() {
		return i;
	}

	public TypeColor() {
		super(Color.class);
	}

	// -------------------------------------------- //
	// WRITE ID
	// -------------------------------------------- //

	@Override
	public @Nullable String getId(@Nullable Color value) {
		if (value == null) return null;
		return value.getRed() + " " + value.getGreen() + " " + value.getBlue();
	}

	// -------------------------------------------- //
	// READ
	// -------------------------------------------- //

	@Override
	public Color read(String arg, CommandSender sender) throws KamiCommonException {
		Color ret;

		// Try RGB
		ret = readInnerRgb(arg, sender);
		if (ret != null) return ret;

		// Try Hex
		ret = readInnerHex(arg);
		if (ret != null) return ret;

        ChatColor error = KamiCommand.Lang.getErrorColor();
        ChatColor param = KamiCommand.Lang.getErrorParamColor();
        throw new KamiCommonException().addMsg(StringUtil.t(error + "No color matches \"" + param + "%s" + error + "\"."), arg);
	}

	public Color readInnerRgb(String arg, CommandSender sender) throws KamiCommonException {
		String[] rgb = Txt.PATTERN_WHITESPACE.split(arg);
		if (rgb.length != 3) return null;

		int red = readInnerRgbNumber(rgb[0], sender);
		int green = readInnerRgbNumber(rgb[1], sender);
		int blue = readInnerRgbNumber(rgb[2], sender);

		return Color.fromRGB(red, green, blue);
	}

	private int readInnerRgbNumber(String arg, CommandSender sender) throws KamiCommonException {
		int ret = TypeInteger.get().read(arg, sender);
		if (ret > 255 || ret < 0) {
            ChatColor error = KamiCommand.Lang.getErrorColor();
            throw new KamiCommonException().addMsg(StringUtil.t(error + "RGB number must be between 0 and 255."));
        }
		return ret;
	}

	public Color readInnerHex(String arg) throws KamiCommonException {
		boolean verbose = false;

		// Explicit verbose hex
		if (arg.startsWith("#")) {
			arg = arg.substring(1);
			verbose = true;
		}

		// Length check 
		if (arg.length() != 6) {
			if (verbose) {
                ChatColor error = KamiCommand.Lang.getErrorColor();
                throw new KamiCommonException().addMsg(StringUtil.t(error + "Hex must be 6 hexadecimals."));
            }
			return null;
		}

		try {
			int red = Integer.parseInt(arg.substring(0, 2), 16);
			int green = Integer.parseInt(arg.substring(2, 4), 16);
			int blue = Integer.parseInt(arg.substring(4, 6), 16);

			return Color.fromRGB(red, green, blue);
		} catch (IllegalArgumentException e) {
			if (verbose) {
                ChatColor error = KamiCommand.Lang.getErrorColor();
                ChatColor param = KamiCommand.Lang.getErrorParamColor();
                throw new KamiCommonException().addMsg(StringUtil.t(error + "\"" + param + "%s" + error + "\" is not valid hexadecimal."), arg);
            }
			return null;
		}
	}

	// -------------------------------------------- //
	// TAB LIST
	// -------------------------------------------- //

	private static final DyeColor[] DYE_COLORS = DyeColor.values();
	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return Arrays.stream(DYE_COLORS).map(Enum::name).filter(name -> name.startsWith(arg.toUpperCase())).collect(Collectors.toList());
	}

}

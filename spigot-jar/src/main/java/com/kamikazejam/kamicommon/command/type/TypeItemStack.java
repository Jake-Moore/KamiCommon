package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.Txt;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.mson.Mson;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TypeItemStack extends TypeAbstract<ItemStack> {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final List<Material> materialsAllowed;

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeItemStack i = new TypeItemStack();

	public static TypeItemStack get() {
		return i;
	}

	public static @NotNull TypeItemStack get(Material... materialWhitelist) {
		return new TypeItemStack(new KamiList<>(materialWhitelist));
	}

	public TypeItemStack(List<Material> materialsAllowed) {
		super(ItemStack.class);
		this.materialsAllowed = materialsAllowed;
	}
	public TypeItemStack() { this(new ArrayList<>()); }

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Mson getVisualMsonInner(ItemStack value, CommandSender sender) {
		return Txt.createItemMson(value);
	}

	@Override
	public String getNameInner(ItemStack value) {
		return null;
	}

	@Override
	public String getIdInner(ItemStack value) {
		return null;
	}

	@Override
	public Collection<String> getTabList(CommandSender sender, String arg) {
		return null;
	}

	@Override
	public ItemStack read(String arg, CommandSender sender) throws KamiCommonException {
		if (!(sender instanceof Player))
			throw new KamiCommonException().addMsg("<b>You must be a player to hold an item in your main hand.");

		Player player = (Player) sender;
		ItemStack ret = NmsVersion.getItemInMainHand(player);
		if (KUtil.isNothing(ret))
			throw new KamiCommonException().addMsg("<b>You must hold an item in your main hand.");

		Material material = ret.getType();
		if (!this.materialsAllowed.contains(material))
			throw new KamiCommonException().addMsg("<h>%s <b>is not allowed.", Txt.getNicedEnum(material));

		ret = new ItemStack(ret);
		return ret;
	}

}

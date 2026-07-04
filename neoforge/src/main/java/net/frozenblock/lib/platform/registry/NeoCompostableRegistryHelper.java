package net.frozenblock.lib.platform.registry;

import net.frozenblock.lib.platform.service.CompostableRegistryHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

public class NeoCompostableRegistryHelper implements CompostableRegistryHelper {

	@Override
	public void add(ItemLike item, float chance) {
		ComposterBlock.COMPOSTABLES.put(item.asItem(), chance);
	}
}

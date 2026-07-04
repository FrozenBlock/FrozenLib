package net.frozenblock.lib.platform;

import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.frozenblock.lib.platform.service.CompostableRegistryHelper;
import net.minecraft.world.level.ItemLike;

public class FabricCompostableRegistryHelper implements CompostableRegistryHelper {

	@Override
	public void add(ItemLike item, float chance) {
		CompostableRegistry.INSTANCE.add(item, chance);
	}
}

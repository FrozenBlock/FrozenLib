package net.frozenblock.lib.platform;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.frozenblock.lib.platform.service.FlammableBlockRegistryHelper;
import net.minecraft.world.level.block.Block;

public class FabricFlammableBlockRegistryHelper implements FlammableBlockRegistryHelper {

	@Override
	public void add(Block block, int igniteOdds, int burnOdds) {
		FlammableBlockRegistry.getDefaultInstance().add(block, igniteOdds, burnOdds);
	}
}

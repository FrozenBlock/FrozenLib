package net.frozenblock.lib.platform;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.frozenblock.lib.platform.service.StrippableBlockRegistryHelper;
import net.minecraft.world.level.block.Block;

public class FabricStrippableBlockRegistryHelper implements StrippableBlockRegistryHelper {

	@Override
	public void register(Block block, Block strippedBlock) {
		StrippableBlockRegistry.register(block, strippedBlock);
	}
}

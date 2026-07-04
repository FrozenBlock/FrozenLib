package net.frozenblock.lib.platform.registry;

import net.frozenblock.lib.platform.service.StrippableBlockRegistryHelper;
import net.frozenblock.lib.registry.mixin.neoforge.AxeItemAccessor;
import net.minecraft.world.level.block.Block;

public class NeoStrippableBlockRegistryHelper implements StrippableBlockRegistryHelper {

	@Override
	public void register(Block block, Block strippedBlock) {
		AxeItemAccessor.frozenLib$getStrippables().put(block, strippedBlock);
	}
}

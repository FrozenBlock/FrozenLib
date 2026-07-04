package net.frozenblock.lib.platform.service;

import net.minecraft.world.level.block.Block;

public interface StrippableBlockRegistryHelper {

	void register(Block block, Block strippedBlock);
}

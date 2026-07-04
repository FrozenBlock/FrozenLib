package net.frozenblock.lib.platform.service;

import net.minecraft.world.level.block.Block;

public interface FlammableBlockRegistryHelper {

	void add(Block block, int igniteOdds, int burnOdds);
}

package net.frozenblock.lib.platform.registry;

import net.frozenblock.lib.platform.service.FlammableBlockRegistryHelper;
import net.frozenblock.lib.registry.mixin.neoforge.FireBlockAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class NeoFlammableBlockRegistryHelper implements FlammableBlockRegistryHelper {

	@Override
	public void add(Block block, int igniteOdds, int burnOdds) {
		FireBlockAccessor fireBlock = (FireBlockAccessor) Blocks.FIRE;
		fireBlock.frozenLib$getIgniteOdds().put(block, igniteOdds);
		fireBlock.frozenLib$getBurnOdds().put(block, burnOdds);
	}
}

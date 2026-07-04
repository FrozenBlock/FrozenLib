package net.frozenblock.lib.registry.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

@UtilityClass
public class FlammableBlockRegistry {

	public static void add(Block block, int igniteOdds, int burnOdds) {
		FrozenLibInitPlatformUtils.FLAMMABLE_BLOCK_REGISTRY.add(block, igniteOdds, burnOdds);
	}
}

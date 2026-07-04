package net.frozenblock.lib.registry.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;

@UtilityClass
public class StrippableBlockRegistry {

	public static void register(Block block, Block strippedBlock) {
		FrozenLibInitPlatformUtils.STRIPPABLE_BLOCK_REGISTRY.register(block, strippedBlock);
	}
}

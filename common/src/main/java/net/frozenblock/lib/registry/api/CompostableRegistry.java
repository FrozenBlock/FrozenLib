package net.frozenblock.lib.registry.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

@UtilityClass
public class CompostableRegistry {

	public static void add(ItemLike item, float chance) {
		FrozenLibInitPlatformUtils.COMPOSTABLE_REGISTRY.add(item, chance);
	}
}

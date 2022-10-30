package net.frozenblock.lib.feature_flag.impl;

import net.minecraft.world.item.ItemStack;

public interface FabricItemGroupAccessor {

	boolean enabled(ItemStack itemStack);
}

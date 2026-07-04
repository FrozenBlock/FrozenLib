package net.frozenblock.lib.platform.service;

import net.minecraft.world.level.ItemLike;

public interface CompostableRegistryHelper {

	void add(ItemLike item, float chance);
}

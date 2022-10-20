package net.frozenblock.lib.item.impl;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;

public interface FrozenTabs {

	CreativeModeTab.ItemDisplayBuilder getDisplayBuilder();

	FeatureFlagSet getFeatureFlagSet();
}

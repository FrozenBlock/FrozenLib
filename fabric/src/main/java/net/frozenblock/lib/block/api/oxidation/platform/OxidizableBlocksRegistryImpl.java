package net.frozenblock.lib.block.api.oxidation.platform;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public final class OxidizableBlocksRegistryImpl {

	public static void registerNextStage(Block from, Block to) {
		OxidizableBlocksRegistry.registerNextStage(from, to);
	}

	public static void registerWaxable(Block unwaxed, Block waxed) {
		OxidizableBlocksRegistry.registerWaxable(unwaxed, waxed);
	}

	public static void registerWeatheringCopperBlocks(WeatheringCopperCollection<Block> copperBlocks) {
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(copperBlocks);
	}
}

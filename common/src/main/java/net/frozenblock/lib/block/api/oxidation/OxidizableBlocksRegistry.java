package net.frozenblock.lib.block.api.oxidation;

import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperCollection;

@UtilityClass
public final class OxidizableBlocksRegistry {

	@PlatformImpl
	public static void registerNextStage(Block from, Block to) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void registerWaxable(Block unwaxed, Block waxed) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void registerWeatheringCopperBlocks(WeatheringCopperCollection<Block> copperBlocks) {
		throw new AssertionError();
	}
}

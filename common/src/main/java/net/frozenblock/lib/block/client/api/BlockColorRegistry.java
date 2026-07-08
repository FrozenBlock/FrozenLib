package net.frozenblock.lib.block.client.api;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.platform.api.registry.FrozenDeferredBlock;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.FoliageColor;

@Environment(EnvType.CLIENT)
public class BlockColorRegistry {

	public static void register(List<BlockTintSource> tintSources, FrozenDeferredBlock<?>... blocks) {
		FrozenLibInitPlatformUtils.BLOCK_COLOR.register(tintSources, blocks);
	}

	public static void registerAverageFoliageTint(FrozenDeferredBlock<?>... blocks) {
		register(List.of(BlockTintSources.foliage()), blocks);
	}

	public static void registerBirchFoliageTint(FrozenDeferredBlock<?>... blocks) {
		register(List.of(BlockTintSources.constant(FoliageColor.FOLIAGE_BIRCH)), blocks);
	}

	public static void registerEvergreenFoliageTint(FrozenDeferredBlock<?>... blocks) {
		register(List.of(BlockTintSources.constant(FoliageColor.FOLIAGE_EVERGREEN)), blocks);
	}

	public static void registerMangroveFoliageTint(FrozenDeferredBlock<?>... blocks) {
		register(List.of(BlockTintSources.constant(FoliageColor.FOLIAGE_MANGROVE)), blocks);
	}

	public static void registerTints(int tint, FrozenDeferredBlock<?>... blocks) {
		register(List.of(BlockTintSources.constant(tint)), blocks);
	}

	public static void registerTints(BlockTintSource tintSource, FrozenDeferredBlock<?>... blocks) {
		register(List.of(tintSource), blocks);
	}

	public static void registerTints(List<BlockTintSource> tintSources, FrozenDeferredBlock<?>... blocks) {
		register(tintSources, blocks);
	}
}

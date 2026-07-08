/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

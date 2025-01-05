/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.block.client;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class TintRegistryHelper {

	public static void registerAverageFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> BiomeColors.getAverageFoliageColor(Objects.requireNonNull(level), Objects.requireNonNull(pos)),
			block
		);
	}

	public static void registerBirchFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> FoliageColor.FOLIAGE_BIRCH,
			block
		);
	}

	public static void registerEvergreenFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> FoliageColor.FOLIAGE_EVERGREEN,
			block
		);
	}

	public static void registerMangroveFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> FoliageColor.FOLIAGE_MANGROVE,
			block
		);
	}

	public static void registerColorForBlock(Block block, int color) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> color,
			block
		);
	}
}

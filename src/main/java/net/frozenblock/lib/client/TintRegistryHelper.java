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

package net.frozenblock.lib.client;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class TintRegistryHelper {

	public static void registerDefaultFoliageColorForItem(ItemLike itemLike) {
		ColorProviderRegistry.ITEM.register(
			(stack, tintIndex) -> FoliageColor.getDefaultColor(),
			itemLike
		);
	}

	public static void registerAverageFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> BiomeColors.getAverageFoliageColor(Objects.requireNonNull(level), Objects.requireNonNull(pos)),
			block
		);
	}

	public static void registerBirchFoliageColorForItem(ItemLike itemLike) {
		ColorProviderRegistry.ITEM.register(
			(stack, tintIndex) -> FoliageColor.getBirchColor(),
			itemLike
		);
	}

	public static void registerBirchFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> FoliageColor.getBirchColor(),
			block
		);
	}

	public static void registerEvergreenFoliageColorForItem(ItemLike itemLike) {
		ColorProviderRegistry.ITEM.register(
			(stack, tintIndex) -> FoliageColor.getEvergreenColor(),
			itemLike
		);
	}

	public static void registerEvergreenFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> FoliageColor.getEvergreenColor(),
			block
		);
	}

	public static void registerMangroveFoliageColorForItem(ItemLike itemLike) {
		ColorProviderRegistry.ITEM.register(
			(stack, tintIndex) -> FoliageColor.getMangroveColor(),
			itemLike
		);
	}

	public static void registerMangroveFoliageColorForBlock(Block block) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> FoliageColor.getMangroveColor(),
			block
		);
	}

	public static void registerColorForItem(ItemLike itemLike, int color) {
		ColorProviderRegistry.ITEM.register(
			(stack, tintIndex) -> color,
			itemLike
		);
	}

	public static void registerColorForBlock(Block block, int color) {
		ColorProviderRegistry.BLOCK.register(
			(state, level, pos, tintIndex) -> color,
			block
		);
	}
}

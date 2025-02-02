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

package net.frozenblock.lib.block.sound.api;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.sound.impl.BlockSoundTypeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Allows you to add any block by either adding its registry (Blocks.STONE) or its ID ("stone").
 * If you want to add a modded block, make sure to put the nameSpaceID in the first field, then the ID and soundGroup.
 * Or you could just be normal and add the block itself instead of the ID.
 * You can also add a LIST of blocks (IDs not allowed) by using new Block[]{block1, block2}.
 */
@UtilityClass
public class BlockSoundTypeOverwrites {
	private static final BlockSoundTypeManager MANAGER = BlockSoundTypeManager.INSTANCE;

	public static Optional<SoundType> getSoundType(Block block) {
		return MANAGER.getSoundType(block);
	}

	public static Optional<SoundType> getSoundType(BlockState blockState) {
		return MANAGER.getSoundType(blockState);
	}

	/**
	 * This will only work with vanilla blocks.
	 */
	public static void addBlock(String id, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBuiltInOverwrite(ResourceLocation.withDefaultNamespace(id), sounds, condition);
	}

	/**
	 * Adds a block with the specified namespace and id.
	 */
	public static void addBlock(String namespace, String path, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBuiltInOverwrite(ResourceLocation.fromNamespaceAndPath(namespace, path), sounds, condition);
	}

	/**
	 * Adds a block with the specified {@link ResourceLocation}.
	 */
	public static void addBlock(ResourceLocation location, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBuiltInOverwrite(location, sounds, condition);
	}

	public static void addBlock(Block block, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBuiltInOverwrite(block, sounds, condition);
	}

	public static void addBlocks(Block[] blocks, SoundType sounds, BooleanSupplier condition) {
		for (Block block : blocks) {
			MANAGER.addBuiltInOverwrite(block, sounds, condition);
		}
	}

	public static void addBlockTag(TagKey<Block> tag, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBuiltInOverwrite(tag, sounds, condition);
	}
}

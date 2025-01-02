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

package net.frozenblock.lib.sound.api.block_sound_group;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.sound.impl.block_sound_group.BlockSoundGroupManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to add any block by either adding its registry (Blocks.STONE) or its ID ("stone").
 * If you want to add a modded block, make sure to put the nameSpaceID in the first field, then the ID and soundGroup.
 * Or you could just be normal and add the block itself instead of the ID.
 * You can also add a LIST of blocks (IDs not allowed) by using new Block[]{block1, block2}.
 */
@UtilityClass
public class BlockSoundGroupOverwrites {

	private static final BlockSoundGroupManager MANAGER = BlockSoundGroupManager.INSTANCE;

	public static List<BlockSoundGroupOverwrite> getOverwrites() {
		return MANAGER.getOverwrites();
	}

	@Nullable
	public static BlockSoundGroupOverwrite getOverwrite(ResourceLocation id) {
		return MANAGER.getOverwrite(id);
	}

	public static @NotNull Optional<BlockSoundGroupOverwrite> getOverwrite(Block block) {
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
		return MANAGER.getOverwrites().stream().filter(overwrite -> overwrite.blockId().equals(id)).findFirst();
	}

	public static Optional<BlockSoundGroupOverwrite> getOverwriteIfConditionIsMet(Block block) {
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
		return MANAGER.getOverwrites().stream().filter(overwrite -> overwrite.blockId().equals(id) && overwrite.condition().getAsBoolean()).findFirst();
	}

	/**
	 * This will only work with vanilla blocks.
	 */
	public static void addBlock(String id, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBlock(id, sounds, condition);
	}

	/**
	 * Adds a block with the specified namespace and id.
	 */
	public static void addBlock(String namespace, String id, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBlock(namespace, id, sounds, condition);
	}

	/**
	 * Adds a block with the specified {@link ResourceLocation}.
	 */
	public static void addBlock(ResourceLocation key, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBlock(key, sounds, condition);
	}

	public static void addBlock(Block block, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBlock(block, sounds, condition);
	}

	public static void addBlocks(Block[] blocks, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBlocks(blocks, sounds, condition);
	}

	public static void addBlockTag(TagKey<Block> tag, SoundType sounds, BooleanSupplier condition) {
		MANAGER.addBlockTag(tag, sounds, condition);
	}
}

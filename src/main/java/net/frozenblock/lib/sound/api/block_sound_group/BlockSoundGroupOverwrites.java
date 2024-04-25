/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.sound.api.block_sound_group;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.frozenblock.lib.sound.impl.block_sound_group.BlockSoundGroupManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to add any block by either adding its registry (Blocks.STONE) or its ID ("stone").
 * If you want to add a modded block, make sure to put the nameSpaceID in the first field, then the ID and soundGroup.
 * Or you could just be normal and add the block itself instead of the ID.
 * You can also add a LIST of blocks (IDs not allowed) by using new Block[]{block1, block2}.
 */
public final class BlockSoundGroupOverwrites {

	private BlockSoundGroupOverwrites() {
		throw new UnsupportedOperationException("BlockSoundGroupOverwrites contains only static declarations.");
	}

	private static final BlockSoundGroupManager MANAGER = BlockSoundGroupManager.INSTANCE;

	@Nullable
	public static List<BlockSoundGroupOverwrite> getOverwrites() {
		return MANAGER.getOverwrites();
	}

	@Nullable
	public static BlockSoundGroupOverwrite getOverwrite(ResourceLocation id) {
		return MANAGER.getOverwrite(id);
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

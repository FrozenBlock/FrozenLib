/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.sound.api.block_sound_group;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

/**
 * Allows you to add any block by either adding its registry (Blocks.STONE) or its ID ("stone").
 * If you want to add a modded block, make sure to put the nameSpaceID (wilderwild) in the first field, then the ID and soundGroup.
 * Or you could just be normal and add the block itself instead of the ID.
 * You can also add a LIST of blocks (IDs not allowed,) by using new Block[]{block1, block2}.
 */
public final class BlockSoundGroupOverwrites {

	private BlockSoundGroupOverwrites() {
		throw new UnsupportedOperationException("BlockSoundGroupOverwrites contains only static declarations.");
	}

	public static final Map<ResourceLocation, SoundType> BLOCK_SOUNDS = new HashMap<>();
	public static final Map<String, SoundType> NAMESPACE_SOUNDS = new HashMap<>();

	/**
	 * This will only work with vanilla blocks.
	 */
	public static void addBlock(String id, SoundType sounds) {
		var key = new ResourceLocation(id);
		if (!Registry.BLOCK.containsKey(key)) {
			throw new IllegalStateException("The specified block's id is null.");
		}
		BLOCK_SOUNDS.put(key, sounds);
	}

	/**
	 * Adds a block with the specified namespace and id.
	 */
	public static void addBlock(String namespace, String id, SoundType sounds) {
		var key = new ResourceLocation(namespace, id);
		if (!Registry.BLOCK.containsKey(key)) {
			throw new IllegalStateException("The specified block's id is null.");
		}
		BLOCK_SOUNDS.put(key, sounds);
	}

	public static void addBlock(Block block, SoundType sounds) {
		var id = Registry.BLOCK.getKey(block);
		if (!Registry.BLOCK.containsKey(id)) {
			throw new IllegalStateException("The specified block's id is null.");
		}
		BLOCK_SOUNDS.put(id, sounds);
	}

	public static void addBlocks(Block[] blocks, SoundType sounds) {
		for (Block block : blocks) {
			var id = Registry.BLOCK.getKey(block);
			if (!Registry.BLOCK.containsKey(id)) {
				throw new IllegalStateException("The specified block's id is null.");
			}
			BLOCK_SOUNDS.put(id, sounds);
		}
	}

	public static void addBlockTag(TagKey<Block> tag, SoundType sounds) {
		var tagIterable = Registry.BLOCK.getTagOrEmpty(tag);
		if (tagIterable == null) {
			throw new IllegalStateException("The specified TagKey is null.");
		} else {
			for (Holder<Block> block : tagIterable) {
				var key = block.unwrapKey().orElseThrow().location();
				if (!Registry.BLOCK.containsKey(key)) {
					throw new IllegalStateException("The specified block's id is null.");
				}
				BLOCK_SOUNDS.put(key, sounds);
			}
		}
	}

	public static void addNamespace(String namespace, SoundType sounds) {
		NAMESPACE_SOUNDS.put(namespace, sounds);
	}
}

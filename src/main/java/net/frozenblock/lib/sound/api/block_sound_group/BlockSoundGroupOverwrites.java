package net.frozenblock.lib.sound.api.block_sound_group;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BlockSoundGroupOverwrites {

	private BlockSoundGroupOverwrites() {
		throw new UnsupportedOperationException("BlockSoundGroupOverwrites contains only static declarations.");
	}

	public static final Map<ResourceLocation, SoundType> BLOCK_SOUNDS = new HashMap<>();
	public static final Map<String, SoundType> NAMESPACE_SOUNDS = new HashMap<>();

	/**
	 * You can add any block by either adding its registry (Blocks.STONE) or its ID ("stone").
	 * If you want to add a modded block, make sure to put the nameSpaceID (wilderwild) in the first field, then the ID and soundGroup.
	 * Or you could just be normal and add the block itself instead of the ID.
	 * You can also add a LIST of blocks (IDs not allowed,) by using new Block[]{block1, block2}.
	 */

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

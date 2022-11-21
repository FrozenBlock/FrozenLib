package net.frozenblock.lib.sound.api.block_sound_group;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import java.util.ArrayList;
import java.util.List;

public final class BlockSoundGroupOverwrites {

	private BlockSoundGroupOverwrites() {
		throw new UnsupportedOperationException("BlockSoundGroupOverwrites contains only static declarations.");
	}

	public static final List<ResourceLocation> IDS = new ArrayList<>();
	public static final List<SoundType> SOUND_GROUPS = new ArrayList<>();
	public static final List<String> NAMESPACES = new ArrayList<>();
	public static final List<SoundType> NAMESPACE_SOUND_GROUPS = new ArrayList<>();

	/**
	 * You can add any block by either adding its registry (Blocks.STONE) or its ID ("stone").
	 * If you want to add a modded block, make sure to put the nameSpaceID (wilderwild) in the first field, then the ID and soundGroup.
	 * Or you could just be normal and add the block itself instead of the ID.
	 * You can also add a LIST of blocks (IDs not allowed,) by using new Block[]{block1, block2}.
	 */

	public static void addBlock(String id, SoundType sounds) {
		IDS.add(new ResourceLocation(id));
		SOUND_GROUPS.add(sounds);
	}

	public static void addBlock(String nameSpace, String id, SoundType sounds) {
		IDS.add(new ResourceLocation(nameSpace, id));
		SOUND_GROUPS.add(sounds);
	}

	public static void addBlock(Block block, SoundType sounds) {
		IDS.add(Registry.BLOCK.getKey(block));
		SOUND_GROUPS.add(sounds);
	}

	public static void addBlocks(Block[] blocks, SoundType sounds) {
		for (Block block : blocks) {
			IDS.add(Registry.BLOCK.getKey(block));
			SOUND_GROUPS.add(sounds);
		}
	}

	public static void addBlockTag(TagKey<Block> tag, SoundType sounds) {
		for (Holder<Block> block : Registry.BLOCK.getTagOrEmpty(tag)) {
			IDS.add(block.unwrapKey().orElseThrow().location());
			SOUND_GROUPS.add(sounds);
		}
	}

	public static void addNamespace(String nameSpace, SoundType sounds) {
		NAMESPACES.add(nameSpace);
		NAMESPACE_SOUND_GROUPS.add(sounds);
	}
}

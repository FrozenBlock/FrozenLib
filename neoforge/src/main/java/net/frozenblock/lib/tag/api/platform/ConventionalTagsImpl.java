package net.frozenblock.lib.tag.api.platform;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public final class ConventionalTagsImpl {

	public static TagKey<Block> chestsBlockTag() {
		return Tags.Blocks.CHESTS;
	}
}

package net.frozenblock.lib.tag.api.platform;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ConventionalTagsImpl {

	public static TagKey<Block> chestsBlockTag() {
		return ConventionalBlockTags.CHESTS;
	}
}

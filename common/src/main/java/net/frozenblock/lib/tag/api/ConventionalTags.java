package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

@UtilityClass
public final class ConventionalTags {

	@PlatformImpl
	public static TagKey<Block> chestsBlockTag() {
		throw new AssertionError();
	}
}

package net.frozenblock.lib.tag.api.platform;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class ConventionalTagsImpl {

	public static TagKey<Block> barrelsBlockTag() {
		return ConventionalBlockTags.BARRELS;
	}

	public static TagKey<Block> barrelsWoodenBlockTag() {
		return ConventionalBlockTags.WOODEN_BARRELS;
	}

	public static TagKey<Block> chestsBlockTag() {
		return ConventionalBlockTags.CHESTS;
	}

	public static TagKey<Block> chestsEnderBlockTag() {
		return ConventionalBlockTags.ENDER_CHESTS;
	}

	public static TagKey<Block> chestsTrappedBlockTag() {
		return ConventionalBlockTags.TRAPPED_CHESTS;
	}

	public static TagKey<Block> chestsWoodenBlockTag() {
		return ConventionalBlockTags.WOODEN_CHESTS;
	}

	public static TagKey<Block> cobblestonesBlockTag() {
		return ConventionalBlockTags.COBBLESTONES;
	}

	public static TagKey<Block> cobblestonesNormalBlockTag() {
		return ConventionalBlockTags.NORMAL_COBBLESTONES;
	}

	public static TagKey<Block> cobblestonesInfestedBlockTag() {
		return ConventionalBlockTags.INFESTED_COBBLESTONES;
	}

	public static TagKey<Block> cobblestonesMossyBlockTag() {
		return ConventionalBlockTags.MOSSY_COBBLESTONES;
	}

	public static TagKey<Block> cobblestonesDeepslateBlockTag() {
		return ConventionalBlockTags.DEEPSLATE_COBBLESTONES;
	}

	public static TagKey<Block> glassBlocksBlockTag() {
		return ConventionalBlockTags.GLASS_BLOCKS;
	}

	public static TagKey<Block> glassBlocksColorlessBlockTag() {
		return ConventionalBlockTags.GLASS_BLOCKS_COLORLESS;
	}

	public static TagKey<Block> glassBlocksCheapBlockTag() {
		return ConventionalBlockTags.GLASS_BLOCKS_CHEAP;
	}

	public static TagKey<Block> glassBlocksTintedBlockTag() {
		return ConventionalBlockTags.GLASS_BLOCKS_TINTED;
	}

	public static TagKey<Block> glassPanesBlockTag() {
		return ConventionalBlockTags.GLASS_PANES;
	}

	public static TagKey<Block> glassPanesColorlessBlockTag() {
		return ConventionalBlockTags.GLASS_PANES_COLORLESS;
	}

	public static TagKey<Block> pumpkinsBlockTag() {
		return ConventionalBlockTags.PUMPKINS;
	}

	public static TagKey<Block> pumpkinsNormalBlockTag() {
		return ConventionalBlockTags.NORMAL_PUMPKINS;
	}

	public static TagKey<Block> pumpkinsCarvedBlockTag() {
		return ConventionalBlockTags.CARVED_PUMPKINS;
	}

	public static TagKey<Block> pumpkinsJackOLanternBlockTag() {
		return ConventionalBlockTags.JACK_O_LANTERNS_PUMPKINS;
	}

	public static TagKey<Block> sandsBlockTag() {
		return ConventionalBlockTags.SANDS;
	}

	public static TagKey<Block> sandsRedBlockTag() {
		return ConventionalBlockTags.RED_SANDS;
	}

	public static TagKey<Block> sandsColorlessBlockTag() {
		return ConventionalBlockTags.COLORLESS_SANDS;
	}

	public static TagKey<Block> sandstoneBlocksBlockTag() {
		return ConventionalBlockTags.SANDSTONE_BLOCKS;
	}

	public static TagKey<Block> sandstoneSlabsBlockTag() {
		return ConventionalBlockTags.SANDSTONE_SLABS;
	}

	public static TagKey<Block> sandstoneStairsBlockTag() {
		return ConventionalBlockTags.SANDSTONE_STAIRS;
	}

	public static TagKey<Block> sandstoneRedBlocksBlockTag() {
		return ConventionalBlockTags.RED_SANDSTONE_BLOCKS;
	}

	public static TagKey<Block> sandstoneRedSlabsBlockTag() {
		return ConventionalBlockTags.RED_SANDSTONE_SLABS;
	}

	public static TagKey<Block> sandstoneRedStairsBlockTag() {
		return ConventionalBlockTags.RED_SANDSTONE_STAIRS;
	}

	public static TagKey<Block> sandstoneUncoloredBlocksBlockTag() {
		return ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS;
	}

	public static TagKey<Block> sandstoneUncoloredSlabsBlockTag() {
		return ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS;
	}

	public static TagKey<Block> sandstoneUncoloredStairsBlockTag() {
		return ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS;
	}

	public static TagKey<Block> stonesBlockTag() {
		return ConventionalBlockTags.STONES;
	}

	public static TagKey<Block> skullsBlockTag() {
		return ConventionalBlockTags.SKULLS;
	}

	public static TagKey<Block> relocationNotSupportedBlockTag() {
		return ConventionalBlockTags.RELOCATION_NOT_SUPPORTED;
	}

	public static TagKey<EntityType<?>> bossesEntityTypeTag() {
		return ConventionalEntityTypeTags.BOSSES;
	}

	public static TagKey<EntityType<?>> minecartsEntityTypeTag() {
		return ConventionalEntityTypeTags.MINECARTS;
	}

	public static TagKey<EntityType<?>> boatsEntityTypeTag() {
		return ConventionalEntityTypeTags.BOATS;
	}

	public static TagKey<EntityType<?>> capturingNotSupportedEntityTypeTag() {
		return ConventionalEntityTypeTags.CAPTURING_NOT_SUPPORTED;
	}

	public static TagKey<EntityType<?>> teleportingNotSupportedEntityTypeTag() {
		return ConventionalEntityTypeTags.TELEPORTING_NOT_SUPPORTED;
	}
}

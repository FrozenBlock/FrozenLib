/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.tag.api.platform;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public final class ConventionalTagsImpl {

	public static TagKey<Block> barrelsBlockTag() {
		return Tags.Blocks.BARRELS;
	}

	public static TagKey<Block> barrelsWoodenBlockTag() {
		return Tags.Blocks.BARRELS_WOODEN;
	}

	public static TagKey<Block> chestsBlockTag() {
		return Tags.Blocks.CHESTS;
	}

	public static TagKey<Block> chestsEnderBlockTag() {
		return Tags.Blocks.CHESTS_ENDER;
	}

	public static TagKey<Block> chestsTrappedBlockTag() {
		return Tags.Blocks.CHESTS_TRAPPED;
	}

	public static TagKey<Block> chestsWoodenBlockTag() {
		return Tags.Blocks.CHESTS_WOODEN;
	}

	public static TagKey<Block> cobblestonesBlockTag() {
		return Tags.Blocks.COBBLESTONES;
	}

	public static TagKey<Block> cobblestonesNormalBlockTag() {
		return Tags.Blocks.COBBLESTONES_NORMAL;
	}

	public static TagKey<Block> cobblestonesInfestedBlockTag() {
		return Tags.Blocks.COBBLESTONES_INFESTED;
	}

	public static TagKey<Block> cobblestonesMossyBlockTag() {
		return Tags.Blocks.COBBLESTONES_MOSSY;
	}

	public static TagKey<Block> cobblestonesDeepslateBlockTag() {
		return Tags.Blocks.COBBLESTONES_DEEPSLATE;
	}

	public static TagKey<Block> glassBlocksBlockTag() {
		return Tags.Blocks.GLASS_BLOCKS;
	}

	public static TagKey<Block> glassBlocksColorlessBlockTag() {
		return Tags.Blocks.GLASS_BLOCKS_COLORLESS;
	}

	public static TagKey<Block> glassBlocksCheapBlockTag() {
		return Tags.Blocks.GLASS_BLOCKS_CHEAP;
	}

	public static TagKey<Block> glassBlocksTintedBlockTag() {
		return Tags.Blocks.GLASS_BLOCKS_TINTED;
	}

	public static TagKey<Block> glassPanesBlockTag() {
		return Tags.Blocks.GLASS_PANES;
	}

	public static TagKey<Block> glassPanesColorlessBlockTag() {
		return Tags.Blocks.GLASS_PANES_COLORLESS;
	}

	public static TagKey<Block> pumpkinsBlockTag() {
		return Tags.Blocks.PUMPKINS;
	}

	public static TagKey<Block> pumpkinsNormalBlockTag() {
		return Tags.Blocks.PUMPKINS_NORMAL;
	}

	public static TagKey<Block> pumpkinsCarvedBlockTag() {
		return Tags.Blocks.PUMPKINS_CARVED;
	}

	public static TagKey<Block> pumpkinsJackOLanternBlockTag() {
		return Tags.Blocks.PUMPKINS_JACK_O_LANTERNS;
	}

	public static TagKey<Block> sandsBlockTag() {
		return Tags.Blocks.SANDS;
	}

	public static TagKey<Block> sandsRedBlockTag() {
		return Tags.Blocks.SANDS_RED;
	}

	public static TagKey<Block> sandsColorlessBlockTag() {
		return Tags.Blocks.SANDS_COLORLESS;
	}

	public static TagKey<Block> sandstoneBlocksBlockTag() {
		return Tags.Blocks.SANDSTONE_BLOCKS;
	}

	public static TagKey<Block> sandstoneSlabsBlockTag() {
		return Tags.Blocks.SANDSTONE_SLABS;
	}

	public static TagKey<Block> sandstoneStairsBlockTag() {
		return Tags.Blocks.SANDSTONE_STAIRS;
	}

	public static TagKey<Block> sandstoneRedBlocksBlockTag() {
		return Tags.Blocks.SANDSTONE_RED_BLOCKS;
	}

	public static TagKey<Block> sandstoneRedSlabsBlockTag() {
		return Tags.Blocks.SANDSTONE_RED_SLABS;
	}

	public static TagKey<Block> sandstoneRedStairsBlockTag() {
		return Tags.Blocks.SANDSTONE_RED_STAIRS;
	}

	public static TagKey<Block> sandstoneUncoloredBlocksBlockTag() {
		return Tags.Blocks.SANDSTONE_UNCOLORED_BLOCKS;
	}

	public static TagKey<Block> sandstoneUncoloredSlabsBlockTag() {
		return Tags.Blocks.SANDSTONE_UNCOLORED_SLABS;
	}

	public static TagKey<Block> sandstoneUncoloredStairsBlockTag() {
		return Tags.Blocks.SANDSTONE_UNCOLORED_STAIRS;
	}

	public static TagKey<Block> stonesBlockTag() {
		return Tags.Blocks.STONES;
	}

	public static TagKey<Block> skullsBlockTag() {
		return Tags.Blocks.SKULLS;
	}

	public static TagKey<Block> relocationNotSupportedBlockTag() {
		return Tags.Blocks.RELOCATION_NOT_SUPPORTED;
	}

	public static TagKey<EntityType<?>> bossesEntityTypeTag() {
		return Tags.EntityTypes.BOSSES;
	}

	public static TagKey<EntityType<?>> minecartsEntityTypeTag() {
		return Tags.EntityTypes.MINECARTS;
	}

	public static TagKey<EntityType<?>> boatsEntityTypeTag() {
		return Tags.EntityTypes.BOATS;
	}

	public static TagKey<EntityType<?>> capturingNotSupportedEntityTypeTag() {
		return Tags.EntityTypes.CAPTURING_NOT_SUPPORTED;
	}

	public static TagKey<EntityType<?>> teleportingNotSupportedEntityTypeTag() {
		return Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED;
	}
}

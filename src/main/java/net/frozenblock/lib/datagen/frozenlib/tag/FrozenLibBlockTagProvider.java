/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.datagen.frozenlib.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public final class FrozenLibBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {

	public FrozenLibBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		this.valueLookupBuilder(FrozenBlockTags.BLOWING_CAN_PASS_THROUGH)
			.addOptionalTag(BlockTags.FENCES)
			.addOptionalTag(BlockTags.FENCE_GATES)
			.addOptionalTag(BlockTags.TRAPDOORS)
			.addOptionalTag(BlockTags.LEAVES)
			.add(Blocks.COPPER_GRATE.unaffected())
			.add(Blocks.COPPER_GRATE.exposed())
			.add(Blocks.COPPER_GRATE.weathered())
			.add(Blocks.COPPER_GRATE.oxidized())
			.add(Blocks.COPPER_GRATE.waxed())
			.add(Blocks.COPPER_GRATE.waxedExposed())
			.add(Blocks.COPPER_GRATE.waxedWeathered())
			.add(Blocks.COPPER_GRATE.waxedOxidized());

		this.valueLookupBuilder(FrozenBlockTags.BLOWING_CANNOT_PASS_THROUGH)
			.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS);
	}

}

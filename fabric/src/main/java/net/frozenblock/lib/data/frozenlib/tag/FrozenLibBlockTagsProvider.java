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

package net.frozenblock.lib.data.frozenlib.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockIds;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;

public final class FrozenLibBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {

	public FrozenLibBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		this.tag(FrozenLibBlockTags.BLOWING_CAN_PASS_THROUGH)
			.addOptionalTag(BlockTags.FENCES)
			.addOptionalTag(BlockTags.FENCE_GATES)
			.addOptionalTag(BlockTags.TRAPDOORS)
			.addOptionalTag(BlockTags.LEAVES)
			.add(BlockItemIds.COPPER_GRATE.asList().stream().map(id -> id.block()).toList().toArray(new ResourceKey[0]));

		this.tag(FrozenLibBlockTags.BLOWING_CANNOT_PASS_THROUGH)
			.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS);

		this.tag(FrozenLibBlockTags.DEFAULT_FIRE_BLOCKS)
			.add(BlockIds.FIRE)
			.add(BlockItemIds.CAMPFIRE.block())
			.add(BlockItemIds.TORCH.block())
			.add(BlockIds.WALL_TORCH);

		this.tag(FrozenLibBlockTags.SOUL_FIRE_BLOCKS);
	}
}

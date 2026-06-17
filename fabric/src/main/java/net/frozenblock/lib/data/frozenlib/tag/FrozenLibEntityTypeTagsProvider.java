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
import net.frozenblock.lib.tag.api.FrozenLibEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityTypeIds;

public final class FrozenLibEntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {

	public FrozenLibEntityTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		this.tag(FrozenLibEntityTypeTags.SCARES_PIGLIN)
			.add(EntityTypeIds.ZOMBIFIED_PIGLIN)
			.add(EntityTypeIds.ZOGLIN);

		this.tag(FrozenLibEntityTypeTags.BLAZES)
			.add(EntityTypeIds.BLAZE);

		this.tag(FrozenLibEntityTypeTags.HOGLINS)
			.add(EntityTypeIds.HOGLIN, EntityTypeIds.ZOGLIN);

		this.tag(FrozenLibEntityTypeTags.GHOST_LIKE)
			.add(EntityTypeIds.VEX);
	}

}

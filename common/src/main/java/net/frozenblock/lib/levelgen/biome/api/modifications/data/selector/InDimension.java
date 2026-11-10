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

package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record InDimension(ResourceKey<LevelStem> dimension) implements BiomeSelector {
	public static final MapCodec<InDimension> CODEC = ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension")
		.xmap(InDimension::new, InDimension::dimension);

	@Override
	public boolean test(BiomeSelectionContext context) {
		return context.canGenerateIn(this.dimension);
	}

	@Override
	public void validate(ValidationContext context) {}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}

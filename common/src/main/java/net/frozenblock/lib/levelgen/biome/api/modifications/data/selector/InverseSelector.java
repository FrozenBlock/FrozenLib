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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record InverseSelector(Holder<BiomeSelector> selector) implements BiomeSelector {
	public static final MapCodec<InverseSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		HOLDER_CODEC.fieldOf("selector").forGetter(predicate -> predicate.selector)
	).apply(instance, InverseSelector::new));

	@Override
	public boolean test(BiomeSelectionContext context) {
		return !this.selector.value().test(context);
	}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "selector", this.selector);
	}
}

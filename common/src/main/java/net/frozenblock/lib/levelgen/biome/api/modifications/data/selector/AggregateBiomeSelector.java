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
import java.util.function.Function;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class AggregateBiomeSelector implements BiomeSelector {
	protected final HolderSet<BiomeSelector> selectors;

	protected AggregateBiomeSelector(HolderSet<BiomeSelector> selectors) {
		this.selectors = selectors;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolderSet(context, "selectors", this.selectors);
	}

	protected static <T extends AggregateBiomeSelector> MapCodec<T> createCodec(Function<HolderSet<BiomeSelector>, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			HOLDER_SET_CODEC.fieldOf("selectors").forGetter(predicate -> predicate.selectors)
		).apply(instance, constructor));
	}
}

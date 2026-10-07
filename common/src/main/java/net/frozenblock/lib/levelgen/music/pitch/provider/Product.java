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

package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Product(HolderSet<PitchProvider> inputs) implements PitchProvider, AggregateProvider<PitchProvider> {
	public static final MapCodec<Product> MAP_CODEC = AggregateProvider.mapCodec(HOLDER_SET_CODEC, Product::new);

	@Override
	public MapCodec<Product> codec() {
		return MAP_CODEC;
	}

	@Override
	public float sample(Context context) {
		float value = 1F;
		for (Holder<PitchProvider> input : this.inputs()) {
			if (input.value().applicable(context)) value *= input.value().sample(context);
		}

		return value;
	}

	@Override
	public boolean applicable(Context context) {
		for (Holder<PitchProvider> input : this.inputs()) {
			if (input.value().applicable(context)) return true;
		}
		return false;
	}
}

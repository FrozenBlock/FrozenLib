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
import net.minecraft.world.level.storage.loot.providers.number.PowerProvider;

public record Power(Holder<PitchProvider> base, Holder<PitchProvider> exponent) implements PitchProvider, PowerProvider<PitchProvider> {
	public static final MapCodec<Power> MAP_CODEC = PowerProvider.mapCodec(HOLDER_CODEC, Power::new);

	@Override
	public MapCodec<Power> codec() {
		return MAP_CODEC;
	}

	@Override
	public float sample(Context context) {
		final float base = this.base.value().sample(context);
		final float exponent = this.exponent.value().sample(context);
		return base == 0F && exponent == 0F ? Float.NaN : (float) Math.pow(base, exponent);
	}

	@Override
	public boolean applicable(Context context) {
		return this.base.value().applicable(context) && this.exponent.value().applicable(context);
	}
}

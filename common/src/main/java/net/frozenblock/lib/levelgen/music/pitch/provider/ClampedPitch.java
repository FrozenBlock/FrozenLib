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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record ClampedPitch(PitchProvider provider, PitchProvider min, PitchProvider max) implements PitchProvider {
	public static MapCodec<ClampedPitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DIRECT_CODEC.fieldOf("provider").forGetter(ClampedPitch::provider),
		DIRECT_CODEC.fieldOf("min").forGetter(ClampedPitch::min),
		DIRECT_CODEC.fieldOf("max").forGetter(ClampedPitch::max)
	).apply(instance, ClampedPitch::new));

	@Override
	public float sample(long gameTime) {
		return Mth.clamp(this.provider.sample(gameTime), this.min.sample(gameTime), this.max.sample(gameTime));
	}

	@Override
	public MapCodec<ClampedPitch> codec() {
		return CODEC;
	}
}

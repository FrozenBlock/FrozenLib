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

public record LerpedPitch(PitchProvider provider, PitchProvider start, PitchProvider end) implements PitchProvider {
	public static MapCodec<LerpedPitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DIRECT_CODEC.fieldOf("provider").forGetter(LerpedPitch::provider),
		DIRECT_CODEC.fieldOf("start").forGetter(LerpedPitch::start),
		DIRECT_CODEC.fieldOf("end").forGetter(LerpedPitch::end)
	).apply(instance, LerpedPitch::new));

	@Override
	public float sample(long gameTime) {
		return Mth.lerp(this.provider.sample(gameTime), this.start.sample(gameTime), this.end.sample(gameTime));
	}

	@Override
	public boolean applicable(Context context) {
		return this.provider.applicable(context) && this.start.applicable(context) && this.end.applicable(context);
	}

	@Override
	public MapCodec<LerpedPitch> codec() {
		return CODEC;
	}
}

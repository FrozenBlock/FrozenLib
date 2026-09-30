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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.Mth;

public abstract class WaveProvider implements PitchProvider {
	protected final float waveLength;
	private final float timeToFunctionValue;

	protected WaveProvider(float waveLength) {
		this.waveLength = waveLength;
		this.timeToFunctionValue = Mth.TWO_PI / this.waveLength;
	}

	@Override
	public final float sample(long gameTime) {
		return this.sampleFunction(gameTime * this.timeToFunctionValue);
	}

	protected abstract float sampleFunction(float value);

	protected static <T extends WaveProvider> MapCodec<T> createCodec(Function<Float, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("wave_length").forGetter(provider -> provider.waveLength)
		).apply(instance, constructor));
	}
}

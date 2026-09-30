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
import java.util.function.BiFunction;

public abstract class OperationProvider implements PitchProvider {
	protected final PitchProvider provider;
	protected final PitchProvider operand;

	protected OperationProvider(PitchProvider provider, PitchProvider operand) {
		this.provider = provider;
		this.operand = operand;
	}

	@Override
	public final float sample(long gameTime) {
		return this.applyOperation(this.provider.sample(gameTime), this.operand.sample(gameTime));
	}

	protected abstract float applyOperation(float sample, float operand);

	@Override
	public boolean applicable(Context context) {
		return this.provider.applicable(context) && this.operand.applicable(context);
	}

	protected static <T extends OperationProvider> MapCodec<T> createCodec(BiFunction<PitchProvider, PitchProvider, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			PitchProvider.DIRECT_CODEC.fieldOf("provider").forGetter(provider -> provider.provider),
			PitchProvider.DIRECT_CODEC.fieldOf("operand").forGetter(provider -> provider.operand)
		).apply(instance, constructor::apply));
	}
}

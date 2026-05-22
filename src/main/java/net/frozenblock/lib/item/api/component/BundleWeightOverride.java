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

package net.frozenblock.lib.item.api.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.math.Fraction;

public final class BundleWeightOverride {
	public static final Codec<BundleWeightOverride> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("numerator").forGetter(component -> component.numerator),
		Codec.INT.fieldOf("denominator").forGetter(BundleWeightOverride::denominator)
	).apply(instance, BundleWeightOverride::new));
	public static final StreamCodec<ByteBuf, BundleWeightOverride> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, BundleWeightOverride::numerator,
		ByteBufCodecs.VAR_INT, BundleWeightOverride::denominator,
		BundleWeightOverride::new
	);
	private final int numerator;
	private final int denominator;
	private final Fraction fraction;

	public BundleWeightOverride(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
		this.fraction = Fraction.getFraction(numerator, denominator);
	}

	public Fraction fraction() {
		return this.fraction;
	}

	protected int numerator() {
		return this.numerator;
	}

	protected int denominator() {
		return this.denominator;
	}
}

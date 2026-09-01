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

package net.frozenblock.lib.particle.api;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

@UtilityClass
public final class ParticleTypeHelper {

	public static SimpleParticleType simple() {
		return simple(false);
	}

	public static SimpleParticleType simple(boolean overrideLimiter) {
		return new SimpleParticleType(overrideLimiter) {};
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		MapCodec<T> codec,
		StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
	) {
		return complex(false, codec, streamCodec);
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		boolean overrideLimiter,
		MapCodec<T> codec,
		StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
	) {
		return new ParticleType<>(overrideLimiter) {
			@Override
			public MapCodec<T> codec() {
				return codec;
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodec;
			}
		};
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		Function<ParticleType<T>, MapCodec<T>> codecGetter,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
	) {
		return complex(false, codecGetter, streamCodecGetter);
	}

	public static <T extends ParticleOptions> ParticleType<T> complex(
		boolean overrideLimiter,
		Function<ParticleType<T>, MapCodec<T>> codecGetter,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
	) {
		return new ParticleType<>(overrideLimiter) {
			@Override
			public MapCodec<T> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodecGetter.apply(this);
			}
		};
	}
}

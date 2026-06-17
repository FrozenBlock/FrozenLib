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

package net.frozenblock.lib.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public record ColoredSmokeParticleOptions(float rDifference, float gDifference, float bDifference, SmokeType smokeType) implements ParticleOptions {
	public static final MapCodec<ColoredSmokeParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("r_difference").forGetter(ColoredSmokeParticleOptions::rDifference),
		Codec.FLOAT.fieldOf("g_difference").forGetter(ColoredSmokeParticleOptions::gDifference),
		Codec.FLOAT.fieldOf("b_difference").forGetter(ColoredSmokeParticleOptions::bDifference),
		SmokeType.CODEC.fieldOf("smoke_type").forGetter(ColoredSmokeParticleOptions::smokeType)
	).apply(instance, ColoredSmokeParticleOptions::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ColoredSmokeParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, ColoredSmokeParticleOptions::rDifference,
		ByteBufCodecs.FLOAT, ColoredSmokeParticleOptions::gDifference,
		ByteBufCodecs.FLOAT, ColoredSmokeParticleOptions::bDifference,
		SmokeType.STREAM_CODEC, ColoredSmokeParticleOptions::smokeType,
		ColoredSmokeParticleOptions::new
	);

	public static ColoredSmokeParticleOptions smoke(float rDifference, float gDifference, float bDifference) {
		return new ColoredSmokeParticleOptions(rDifference, gDifference, bDifference, SmokeType.SMOKE);
	}

	public static ColoredSmokeParticleOptions largeSmoke(float rDifference, float gDifference, float bDifference) {
		return new ColoredSmokeParticleOptions(rDifference, gDifference, bDifference, SmokeType.LARGE_SMOKE);
	}

	public static ColoredSmokeParticleOptions campfireCosy(float rDifference, float gDifference, float bDifference) {
		return new ColoredSmokeParticleOptions(rDifference, gDifference, bDifference, SmokeType.CAMPFIRE_COSY);
	}

	public static ColoredSmokeParticleOptions campfireSignal(float rDifference, float gDifference, float bDifference) {
		return new ColoredSmokeParticleOptions(rDifference, gDifference, bDifference, SmokeType.CAMPFIRE_SIGNAL);
	}

	public enum SmokeType implements StringRepresentable {
		SMOKE("smoke"),
		LARGE_SMOKE("large_smoke"),
		CAMPFIRE_COSY("campfire_cosy"),
		CAMPFIRE_SIGNAL("campfire_signal");
		public static final Codec<SmokeType> CODEC = StringRepresentable.fromEnum(SmokeType::values);
		public static final StreamCodec<ByteBuf, SmokeType> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public SmokeType decode(ByteBuf byteBuf) {
				return SmokeType.valueOf(ByteBufCodecs.STRING_UTF8.decode(byteBuf));
			}

			@Override
			public void encode(ByteBuf byteBuf, SmokeType particleLength) {
				ByteBufCodecs.STRING_UTF8.encode(byteBuf, particleLength.name());
			}
		};
		private final String name;

		SmokeType(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

	@Override
	public ParticleType<?> getType() {
		return switch (this.smokeType) {
			case SMOKE -> FrozenLibParticleTypes.SMOKE_COLORED;
			case LARGE_SMOKE -> FrozenLibParticleTypes.LARGE_SMOKE_COLORED;
			case CAMPFIRE_COSY -> FrozenLibParticleTypes.CAMPFIRE_COSY_SMOKE_COLORED;
			case CAMPFIRE_SIGNAL -> FrozenLibParticleTypes.CAMPFIRE_SIGNAL_SMOKE_COLORED;
		};
	}
}

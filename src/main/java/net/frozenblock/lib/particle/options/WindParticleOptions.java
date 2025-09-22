/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.frozenblock.lib.networking.FrozenByteBufCodecs;
import net.frozenblock.lib.particle.FrozenLibParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class WindParticleOptions implements ParticleOptions {
	public static final MapCodec<WindParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			Codec.INT.fieldOf("lifespan").forGetter(WindParticleOptions::getLifespan),
			Vec3.CODEC.fieldOf("velocity").forGetter(WindParticleOptions::getVelocity),
			ParticleLength.CODEC.fieldOf("length").forGetter(WindParticleOptions::length)
		).apply(instance, WindParticleOptions::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, WindParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, WindParticleOptions::getLifespan,
		FrozenByteBufCodecs.VEC3, WindParticleOptions::getVelocity,
		ParticleLength.STREAM_CODEC, WindParticleOptions::length,
		WindParticleOptions::new
	);

	private final int lifespan;
	private final Vec3 velocity;
	private final ParticleLength length;

	public WindParticleOptions(int lifespan, Vec3 velocity, ParticleLength length) {
		this.lifespan = lifespan;
		this.velocity = velocity;
		this.length = length;
	}

	public WindParticleOptions(int lifespan, Vec3 velocity) {
		this.lifespan = lifespan;
		this.velocity = velocity;
		this.length = ParticleLength.SMALL;
	}

	public WindParticleOptions(int lifespan, double xVel, double yVel, double zVel, ParticleLength length) {
		this.lifespan = lifespan;
		this.velocity = new Vec3(xVel, yVel, zVel);
		this.length = length;
	}

	public WindParticleOptions(int lifespan, double xVel, double yVel, double zVel) {
		this.lifespan = lifespan;
		this.velocity = new Vec3(xVel, yVel, zVel);
		this.length = ParticleLength.SMALL;
	}

	@NotNull
	@Override
	public ParticleType<?> getType() {
		if (this.length == ParticleLength.MEDIUM) return FrozenLibParticleTypes.WIND_MEDIUM;
		return FrozenLibParticleTypes.WIND_SMALL;
	}

	public int getLifespan() {
		return this.lifespan;
	}

	public Vec3 getVelocity() {
		return this.velocity;
	}

	public ParticleLength length() {
		return this.length;
	}

	public enum ParticleLength implements StringRepresentable {
		SMALL("small", 1D, 0.25F, 1F),
		MEDIUM("medium", 1.2D, 0.1F, 3.2F);
		public static final Codec<ParticleLength> CODEC = StringRepresentable.fromEnum(ParticleLength::values);
		public static final StreamCodec<ByteBuf, ParticleLength> STREAM_CODEC = new StreamCodec<>() {
			@Override
			public @NotNull ParticleLength decode(@NotNull ByteBuf byteBuf) {
				return ParticleLength.valueOf(ByteBufCodecs.STRING_UTF8.decode(byteBuf));
			}

			@Override
			public void encode(@NotNull ByteBuf byteBuf, @NotNull ParticleLength particleLength) {
				ByteBufCodecs.STRING_UTF8.encode(byteBuf, particleLength.name());
			}
		};

		private final String name;
		private final double windMovementScale;
		private final float rotationChangeAmount;
		private final float quadSizeScale;

		ParticleLength(String name, double windMovementScale, float rotationChangeAmount, float quadSizeScale) {
			this.name = name;
			this.windMovementScale = windMovementScale;
			this.rotationChangeAmount = rotationChangeAmount;
			this.quadSizeScale = quadSizeScale;
		}

		public double getWindMovementScale() {
			return this.windMovementScale;
		}

		public float getRotationChangeAmount() {
			return this.rotationChangeAmount;
		}

		public float getQuadSizeScale() {
			return this.quadSizeScale;
		}

		@Contract(pure = true)
		@Override
		public @NotNull String getSerializedName() {
			return this.name;
		}
	}

}

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

package net.frozenblock.lib.math.api;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.phys.Vec3;

/**
 * Adds easy-to-use noise sampling and random number generators
 */
@UtilityClass
public class EasyNoiseSampler {

	public static PerlinNoise createCheckedNoise(long seed) {
		return new PerlinNoise(new LegacyRandomSource(seed));
	}

	public static PerlinNoise createLegacyThreadSafeNoise(long seed) {
		return new PerlinNoise(new ThreadSafeLegacyRandomSource(seed));
	}

	public static PerlinNoise createLocalNoise(long seed) {
		return new PerlinNoise(new SingleThreadedRandomSource(seed));
	}

	public static PerlinNoise createXoroNoise(long seed) {
		return new PerlinNoise(new XoroshiroRandomSource(seed));
	}

    public static double sample(PerlinNoise sampler, Vec3i pos, double multiplier, boolean multiplyY, boolean useY) {
        if (useY) {
            if (multiplyY) return sampler.get(pos.getX() * multiplier, pos.getY() * multiplier, pos.getZ() * multiplier);
            return sampler.get(pos.getX() * multiplier, pos.getY(), pos.getZ() * multiplier);
        }
        return sampler.get(pos.getX() * multiplier, 64, pos.getZ() * multiplier);
    }

    public static double sampleAbs(PerlinNoise sampler, Vec3i pos, double multiplier, boolean multiplyY, boolean useY) {
        return Math.abs(sample(sampler, pos, multiplier, multiplyY, useY));
    }

	public static double sample(PerlinNoise sampler, Vec3 pos, double multiplier, boolean multiplyY, boolean useY) {
		if (useY) {
			if (multiplyY) return sampler.get(pos.x() * multiplier, pos.y() * multiplier, pos.z() * multiplier);
			return sampler.get(pos.x() * multiplier, pos.y(), pos.z() * multiplier);
		}
		return sampler.get(pos.x() * multiplier, 64, pos.z() * multiplier);
	}

	public enum NoiseType implements StringRepresentable {
		CHECKED("CHECKED", EasyNoiseSampler::createCheckedNoise),
		LEGACY_THREAD_SAFE("LEGACY_THREAD_SAFE", EasyNoiseSampler::createLegacyThreadSafeNoise),
		LOCAL("LOCAL", EasyNoiseSampler::createLocalNoise),
		XORO("XORO", EasyNoiseSampler::createXoroNoise);
		public static final Codec<NoiseType> CODEC = StringRepresentable.fromEnum(NoiseType::values);

		private final Function<Long, PerlinNoise> noiseSupplier;
		private final String serializationKey;

		NoiseType(String serializationKey, Function<Long, PerlinNoise> noiseSupplier) {
			this.serializationKey = serializationKey;
			this.noiseSupplier = noiseSupplier;
		}

		public PerlinNoise createNoise(long seed) {
			return this.noiseSupplier.apply(seed);
		}

		@Override
		public String getSerializedName() {
			return this.serializationKey;
		}
	}
}

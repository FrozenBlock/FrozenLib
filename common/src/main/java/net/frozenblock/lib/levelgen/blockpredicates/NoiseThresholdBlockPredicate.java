/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.levelgen.blockpredicates.impl.FrozenLibBlockPredicateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public record NoiseThresholdBlockPredicate(ResourceKey<NormalNoise> noise, Optional<Double> minThreshold, Optional<Double> maxThreshold, boolean is3d) implements BlockPredicate {
	public static final MapCodec<NoiseThresholdBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdBlockPredicate::noise),
		Codec.DOUBLE.optionalFieldOf("min_threshold").forGetter(NoiseThresholdBlockPredicate::minThreshold),
		Codec.DOUBLE.optionalFieldOf("max_threshold").forGetter(NoiseThresholdBlockPredicate::maxThreshold),
		Codec.BOOL.optionalFieldOf("is_3d", false).forGetter(NoiseThresholdBlockPredicate::is3d)
	).apply(instance, NoiseThresholdBlockPredicate::new));

	@Override
	public BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.NOISE_THRESHOLD.get();
	}

	public static NoiseThresholdBlockPredicate ofMinThreshold(ResourceKey<NormalNoise> noise, double minThreshold, boolean is3d) {
		return new NoiseThresholdBlockPredicate(noise, Optional.of(minThreshold), Optional.empty(), is3d);
	}

	public static NoiseThresholdBlockPredicate ofMaxThreshold(ResourceKey<NormalNoise> noise, double maxThreshold, boolean is3d) {
		return new NoiseThresholdBlockPredicate(noise, Optional.empty(), Optional.of(maxThreshold), is3d);
	}


	public static NoiseThresholdBlockPredicate of(ResourceKey<NormalNoise> noise, double minThreshold, double maxThreshold, boolean is3d) {
		return new NoiseThresholdBlockPredicate(noise, Optional.of(minThreshold), Optional.of(maxThreshold), is3d);
	}

	@Override
	public boolean test(LevelAccessor level, BlockPos pos) {
		final double value = getNoiseSampler(level, pos, this.noise, this.is3d);
		if (this.minThreshold.isPresent() && value < this.minThreshold.get()) return false;
		if (this.maxThreshold.isPresent() && value > this.maxThreshold.get()) return false;
		return true;
	}

	private static double getNoiseSampler(LevelAccessor level, BlockPos pos, ResourceKey<NormalNoise> noiseId, boolean is3d) {
		return is3d
			? createNoiseSampler3d(level, pos, noiseId)
			: createNoiseSampler2d(level, pos, noiseId);
	}

	private static double createNoiseSampler2d(LevelAccessor level, BlockPos pos, ResourceKey<NormalNoise> noiseId) {
		final Noise noise;
		if (level instanceof ServerLevelAccessor serverLevelAccessor) {
			noise = serverLevelAccessor.getLevel().getChunkSource().randomState().getOrCreateNoise(noiseId);
		} else {
			throw new IllegalStateException("Attempted to find randomState from non-server level");
		}

		return noise.get(pos.getX(), 0, pos.getZ());
	}

	private static double createNoiseSampler3d(LevelAccessor level, BlockPos pos, ResourceKey<NormalNoise> noiseId) {
		final Noise noise;
		if (level instanceof ServerLevelAccessor serverLevelAccessor) {
			noise = serverLevelAccessor.getLevel().getChunkSource().randomState().getOrCreateNoise(noiseId);
		} else {
			throw new IllegalStateException("Attempted to find randomState from non-server level");
		}

		return noise.get(pos.getX(), pos.getY(), pos.getZ());
	}
}

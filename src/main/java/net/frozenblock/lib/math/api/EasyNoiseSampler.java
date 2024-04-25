/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.math.api;

import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;

/**
 * Adds easy-to-use noise sampling and random number generators
 *
 * @author Lunade (2021-2022)
 */
public final class EasyNoiseSampler {

	private EasyNoiseSampler() {
		throw new UnsupportedOperationException("EasyNoiseSampler contains only static declarations.");
	}

    public static long seed = 0;
    public static RandomSource checkedRandom = new LegacyRandomSource(seed);
    public static RandomSource threadSafeRandom = new ThreadSafeLegacyRandomSource(seed);
    public static RandomSource localRandom = new SingleThreadedRandomSource(seed);
    public static XoroshiroRandomSource xoroRandom = new XoroshiroRandomSource(seed);
    public static ImprovedNoise perlinChecked = new ImprovedNoise(checkedRandom);
    public static ImprovedNoise perlinThreadSafe = new ImprovedNoise(threadSafeRandom);
    public static ImprovedNoise perlinLocal = new ImprovedNoise(localRandom);
    public static ImprovedNoise perlinXoro = new ImprovedNoise(xoroRandom);

    public static double sample(ImprovedNoise sampler, Vec3i pos, double multiplier, boolean multiplyY, boolean useY) {
        if (useY) {
            if (multiplyY) {
                return sampler.noise(pos.getX() * multiplier, pos.getY() * multiplier, pos.getZ() * multiplier);
            }
            return sampler.noise(pos.getX() * multiplier, pos.getY(), pos.getZ() * multiplier);
        }
        return sampler.noise(pos.getX() * multiplier, 64, pos.getZ() * multiplier);
    }

    public static double sampleAbs(ImprovedNoise sampler, Vec3i pos, double multiplier, boolean multiplyY, boolean useY) {
        return Math.abs(sample(sampler, pos, multiplier, multiplyY, useY));
    }

	public static double sample(WorldGenLevel level, ImprovedNoise sampler, Vec3i pos, double multiplier, boolean multiplyY, boolean useY) {
		setSeed(level);
		return sample(sampler, pos, multiplier, multiplyY, useY);
	}

	public static double sampleAbs(WorldGenLevel level, ImprovedNoise sampler, Vec3i pos, double multiplier, boolean multiplyY, boolean useY) {
		setSeed(level);
		return sampleAbs(sampler, pos, multiplier, multiplyY, useY);
	}

	public static double sample(ImprovedNoise sampler, Vec3 pos, double multiplier, boolean multiplyY, boolean useY) {
		if (useY) {
			if (multiplyY) {
				return sampler.noise(pos.x() * multiplier, pos.y() * multiplier, pos.z() * multiplier);
			}
			return sampler.noise(pos.x() * multiplier, pos.y(), pos.z() * multiplier);
		}
		return sampler.noise(pos.x() * multiplier, 64, pos.z() * multiplier);
	}

	public static double sampleAbs(ImprovedNoise sampler, Vec3 pos, double multiplier, boolean multiplyY, boolean useY) {
		return Math.abs(sample(sampler, pos, multiplier, multiplyY, useY));
	}

	public static double sample(WorldGenLevel level, ImprovedNoise sampler, Vec3 pos, double multiplier, boolean multiplyY, boolean useY) {
		setSeed(level);
		return sample(sampler, pos, multiplier, multiplyY, useY);
	}

	public static double sampleAbs(WorldGenLevel level, ImprovedNoise sampler, Vec3 pos, double multiplier, boolean multiplyY, boolean useY) {
		setSeed(level);
		return sampleAbs(sampler, pos, multiplier, multiplyY, useY);
	}

	public static void setSeed(long newSeed) {
		if (newSeed != seed) {
			seed = newSeed;
			checkedRandom = new LegacyRandomSource(seed);
			threadSafeRandom = new ThreadSafeLegacyRandomSource(seed);
			localRandom = new SingleThreadedRandomSource(seed);
			xoroRandom = new XoroshiroRandomSource(seed);
			perlinChecked = new ImprovedNoise(checkedRandom);
			perlinThreadSafe = new ImprovedNoise(threadSafeRandom);
			perlinLocal = new ImprovedNoise(localRandom);
			perlinXoro = new ImprovedNoise(xoroRandom);
		}
	}

	public static void setSeed(WorldGenLevel level) {
		long newSeed = level.getSeed();
		if (newSeed != seed) {
			seed = newSeed;
			checkedRandom = new LegacyRandomSource(seed);
			threadSafeRandom = new ThreadSafeLegacyRandomSource(seed);
			localRandom = new SingleThreadedRandomSource(seed);
			xoroRandom = new XoroshiroRandomSource(seed);
			perlinChecked = new ImprovedNoise(checkedRandom);
			perlinThreadSafe = new ImprovedNoise(threadSafeRandom);
			perlinLocal = new ImprovedNoise(localRandom);
			perlinXoro = new ImprovedNoise(xoroRandom);
		}
	}

}

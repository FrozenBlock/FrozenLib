/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
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

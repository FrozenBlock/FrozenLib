/*
 * Copyright 2023 FrozenBlock
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

import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.white.WhiteNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;

/**
 * Adds easy-to-use noise sampling and random number generators
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

	public static void sampleTest() {
		setSeed(RandomSource.create().nextLong());
		FrozenMain.LOGGER.info("VANILLA NOISES:");
		long started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinChecked.noise(d, 0, 0);
		}
		FrozenMain.LOGGER.info("PerlinChecked took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinThreadSafe.noise(d, 0, 0);
		}
		FrozenMain.LOGGER.info("PerlinThreadSafe took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinLocal.noise(d, 0, 0);
		}
		FrozenMain.LOGGER.info("Perlinlocal took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinXoro.noise(d, 0, 0);
		}
		FrozenMain.LOGGER.info("PerlinXoro took {} nanoseconds.", System.nanoTime() - started);

		FrozenMain.LOGGER.info("JNOISE NOISES:");
		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinCosine.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("PerlinCosine took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinLinear.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("PerlinLineaar took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinQuadratic.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("PerlinQuadratic took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			whiteNoise.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("White took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinCosineFade.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("CosineFade took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinLinearFade.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("LinearFade took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinQuadraticFade.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("QuadraticFade took {} nanoseconds.", System.nanoTime() - started);

		started = System.nanoTime();
		for (double d = -100; d < 100; d += 0.25) {
			perlinQuarticFade.evaluateNoise(d);
		}
		FrozenMain.LOGGER.info("QuarticFade took {} nanoseconds.", System.nanoTime() - started);
	}

	public static PerlinNoiseGenerator perlinCosine = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.COSINE).build();
	public static PerlinNoiseGenerator perlinLinear = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.LINEAR).build();
	public static PerlinNoiseGenerator perlinQuadratic = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.QUADRATIC).build();
	public static PerlinNoiseGenerator perlinQuartic = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.QUARTIC).build();
	public static WhiteNoiseGenerator whiteNoise = WhiteNoiseGenerator.newBuilder().setSeed(seed).build();
	public static JNoise perlinCosineFade = JNoise.newBuilder().perlin(seed, Interpolation.COSINE, FadeFunction.IMPROVED_PERLIN_NOISE).build();
	public static JNoise perlinLinearFade = JNoise.newBuilder().perlin(seed, Interpolation.LINEAR, FadeFunction.IMPROVED_PERLIN_NOISE).build();
	public static JNoise perlinQuadraticFade = JNoise.newBuilder().perlin(seed, Interpolation.QUADRATIC, FadeFunction.IMPROVED_PERLIN_NOISE).build();
	public static JNoise perlinQuarticFade = JNoise.newBuilder().perlin(seed, Interpolation.QUARTIC, FadeFunction.IMPROVED_PERLIN_NOISE).build();

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

			perlinCosine = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.COSINE).build();
			perlinLinear = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.LINEAR).build();
			perlinQuadratic = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.QUADRATIC).build();
			perlinQuartic = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.QUARTIC).build();
			whiteNoise = WhiteNoiseGenerator.newBuilder().setSeed(seed).build();
			perlinCosineFade = JNoise.newBuilder().perlin(seed, Interpolation.COSINE, FadeFunction.IMPROVED_PERLIN_NOISE).build();
			perlinLinearFade = JNoise.newBuilder().perlin(seed, Interpolation.LINEAR, FadeFunction.IMPROVED_PERLIN_NOISE).build();
			perlinQuadraticFade = JNoise.newBuilder().perlin(seed, Interpolation.QUADRATIC, FadeFunction.IMPROVED_PERLIN_NOISE).build();
			perlinQuarticFade = JNoise.newBuilder().perlin(seed, Interpolation.QUARTIC, FadeFunction.IMPROVED_PERLIN_NOISE).build();
		}
	}

    public static double sample(ImprovedNoise sampler, BlockPos pos, double multiplier, boolean multiplyY, boolean useY) {
        if (useY) {
            if (multiplyY) {
                return sampler.noise(pos.getX() * multiplier, pos.getY() * multiplier, pos.getZ() * multiplier);
            }
            return sampler.noise(pos.getX() * multiplier, pos.getY(), pos.getZ() * multiplier);
        }
        return sampler.noise(pos.getX() * multiplier, 64, pos.getZ() * multiplier);
    }

    public static double samplePositive(ImprovedNoise sampler, BlockPos pos, double multiplier, boolean multiplyY, boolean useY) {
        double ret = 0;
        if (useY) {
            if (multiplyY) {
                ret = sampler.noise(pos.getX() * multiplier, pos.getY() * multiplier, pos.getZ() * multiplier);
            } else {
                ret = sampler.noise(pos.getX() * multiplier, pos.getY(), pos.getZ() * multiplier);
            }
        } else {
            ret = sampler.noise(pos.getX() * multiplier, 64, pos.getZ() * multiplier);
        }
        if (ret < 0) {
            return ret * -1;
        }
        return ret;
    }

	public static Vec3 sampleVec3(ImprovedNoise sampler, double x, double y, double z) {
		double windX = sampler.noise(x, 0, 0);
		double windY = sampler.noise(0, y, 0);
		double windZ = sampler.noise(0, 0, z);
		return new Vec3(windX, windY, windZ);
	}

}

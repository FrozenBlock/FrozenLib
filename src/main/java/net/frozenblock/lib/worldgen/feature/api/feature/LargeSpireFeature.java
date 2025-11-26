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

package net.frozenblock.lib.worldgen.feature.api.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.frozenblock.lib.worldgen.feature.api.feature.config.LargeSpireConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LargeSpireFeature extends Feature<LargeSpireConfig> {

	public LargeSpireFeature(Codec<LargeSpireConfig> codec) {
		super(codec);
	}

	private static LargeSpireFeature.LargeSpire make(
		BlockPos root,
		boolean pointingUp,
		RandomSource random,
		int radius,
		FloatProvider bluntnessBase,
		FloatProvider scaleBase
	) {
		return new LargeSpire(root, pointingUp, radius, bluntnessBase.sample(random), scaleBase.sample(random));
	}

	protected static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel level, BlockPos pos, int radius) {
		if (isEmptyOrWaterOrLava(level, pos)) return false;

		final float increment = 6F / (float) radius;
		for (float f = 0F; f < 6.2831855F; f += increment) {
			final int xOffset = (int) (Mth.cos(f) * (float) radius);
			final int zOffset = (int) (Mth.sin(f) * (float) radius);
			if (isEmptyOrWaterOrLava(level, pos.offset(xOffset, 0, zOffset))) return false;
		}

		return true;
	}

	protected static boolean isEmptyOrWaterOrLava(LevelAccessor level, BlockPos pos) {
		return level.isStateAtPosition(pos, LargeSpireFeature::isEmptyOrWaterOrLava);
	}

	public static boolean isEmptyOrWaterOrLava(BlockState state) {
		return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
	}

	protected static double getHeight(double radius, double maxRadius, double scale, double minRadius) {
		if (radius < minRadius) radius = minRadius;
		final double e = radius / maxRadius * 0.384D;
		final double f = 0.75D * Math.pow(e, 1.3333333333333333D);
		final double g = Math.pow(e, 0.6666666666666666D);
		final double h = 0.3333333333333333D * Math.log(e);
		final double i = Math.max(scale * (f - g - h), 0D);
		return i / 0.384D * maxRadius;
	}

	@Override
	public boolean place(FeaturePlaceContext<LargeSpireConfig> context) {
		final WorldGenLevel level = context.level();
		final BlockPos pos = context.origin();
		final LargeSpireConfig config = context.config();
		final RandomSource random = context.random();

		if (!LargeSpireFeature.isEmptyOrWaterOrLava(level, pos)) return false;

		final Optional<Column> optionalColumn = Column.scan(
			level,
			pos,
			config.floorToCeilingSearchRange(),
			DripstoneUtils::isEmptyOrWaterOrLava,
			state -> LargeSpireFeature.isBaseOrLava(config, state)
		);

		if (optionalColumn.isEmpty() || !(optionalColumn.get() instanceof Column.Range range)) return false;
		if (range.height() < 4) return false;

		final int radiusByHeight = (int) ((float) range.height() * config.maxColumnRadiusToCaveHeightRatio());
		final int clampedRadius = Mth.clamp(radiusByHeight, config.columnRadius().getMinValue(), config.columnRadius().getMaxValue());
		final int radius = Mth.randomBetweenInclusive(random, config.columnRadius().getMinValue(), clampedRadius);

		final LargeSpire ceilingSpire = make(pos.atY(range.ceiling() - 1), false, random, radius, config.stalactiteBluntness(), config.heightScale());
		final LargeSpire floorSpire = make(pos.atY(range.floor() + 1), true, random, radius, config.stalagmiteBluntness(), config.heightScale());

		final WindOffsetter windOffsetter = ceilingSpire.isSuitableForWind(config) && floorSpire.isSuitableForWind(config)
			? new WindOffsetter(pos.getY(), random, config.windSpeed())
			: WindOffsetter.noWind();

		if (ceilingSpire.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(level, windOffsetter)) ceilingSpire.placeBlocks(level, random, windOffsetter, config);
		if (floorSpire.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(level, windOffsetter)) floorSpire.placeBlocks(level, random, windOffsetter, config);

		return true;
	}

	public static boolean isBaseOrLava(LargeSpireConfig config, BlockState state) {
		return isBase(config, state) || state.is(Blocks.LAVA);
	}

	public static boolean isBase(LargeSpireConfig config, BlockState state) {
		return state.is(config.baseBlocks()) || state.is(config.replaceable());
	}

	static final class LargeSpire {
		private final boolean pointingUp;
		private final double bluntness;
		private final double scale;
		private BlockPos root;
		private int radius;

		LargeSpire(BlockPos root, boolean pointingUp, int radius, double bluntness, double scale) {
			this.root = root;
			this.pointingUp = pointingUp;
			this.radius = radius;
			this.bluntness = bluntness;
			this.scale = scale;
		}

		private int getHeight() {
			return this.getHeightAtRadius(0F);
		}

		boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(WorldGenLevel level, WindOffsetter windOffsetter) {
			while (this.radius > 1) {
				final BlockPos.MutableBlockPos mutable = this.root.mutable();
				int searchRange = Math.min(10, this.getHeight());

				for (int j = 0; j < searchRange; ++j) {
					if (LargeSpireFeature.isCircleMostlyEmbeddedInStone(level, windOffsetter.offset(mutable), this.radius)) {
						this.root = mutable;
						return true;
					}
					mutable.move(this.pointingUp ? Direction.DOWN : Direction.UP);
				}

				this.radius /= 2;
			}

			return false;
		}

		private int getHeightAtRadius(float radius) {
			return (int) LargeSpireFeature.getHeight(radius, this.radius, this.scale, this.bluntness);
		}

		void placeBlocks(WorldGenLevel level, RandomSource random, WindOffsetter windOffsetter, LargeSpireConfig config) {
			for (int x = -this.radius; x <= this.radius; ++x) {
				for (int z = -this.radius; z <= this.radius; ++z) {
					final float distance = Mth.sqrt((float) (x * x + z * z));
					if (distance > (float) this.radius) continue;

					int heightAtRadius = this.getHeightAtRadius(distance);
					if (heightAtRadius <= 0) continue;

					if (random.nextFloat() < 0.2F) heightAtRadius = (int) ((float) heightAtRadius * Mth.randomBetween(random, 0.8F, 1F));

					final BlockPos.MutableBlockPos mutable = this.root.offset(x, 0, z).mutable();
					boolean bl = false;
					int searchAttempts = this.pointingUp ? level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutable.getX(), mutable.getZ()) : Integer.MAX_VALUE;

					for (int i = 0; i < heightAtRadius && mutable.getY() < searchAttempts; ++i) {
						final BlockPos pos = windOffsetter.offset(mutable);
						if (isEmptyOrWaterOrLava(level, pos)) {
							bl = true;
							level.setBlock(pos, config.pathBlock().getState(random, mutable), Block.UPDATE_ALL);
						} else if (bl && level.getBlockState(pos).is(BlockTags.BASE_STONE_NETHER)) {
							break;
						}

						mutable.move(this.pointingUp ? Direction.UP : Direction.DOWN);
					}
				}
			}
		}

		boolean isSuitableForWind(LargeSpireConfig config) {
			return this.radius >= config.minRadiusForWind() && this.bluntness >= (double) config.minBluntnessForWind();
		}
	}

	private static final class WindOffsetter {
		private final int originY;
		@Nullable
		private final Vec3 windSpeed;

		WindOffsetter(int originY, RandomSource random, FloatProvider magnitude) {
			this.originY = originY;
			final float magnitudeSample = magnitude.sample(random);
			final float circleSample = Mth.randomBetween(random, 0F, 3.1415927F);
			this.windSpeed = new Vec3(Mth.cos(circleSample) * magnitudeSample, 0D, Mth.sin(circleSample) * magnitudeSample);
		}

		private WindOffsetter() {
			this.originY = 0;
			this.windSpeed = null;
		}

		static WindOffsetter noWind() {
			return new WindOffsetter();
		}

		BlockPos offset(BlockPos pos) {
			if (this.windSpeed == null) return pos;
			final Vec3 vec3 = this.windSpeed.scale(this.originY - pos.getY());
			return pos.offset(BlockPos.containing(vec3.x, 0D, vec3.z));
		}
	}
}


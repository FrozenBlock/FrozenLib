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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class LargeSpireFeature extends Feature<LargeSpireConfig> {

	public LargeSpireFeature(Codec<LargeSpireConfig> codec) {
		super(codec);
	}

	@NotNull
	private static LargeSpireFeature.LargeSpire make(
		@NotNull BlockPos root,
		boolean pointingUp,
		@NotNull RandomSource random,
		int radius,
		@NotNull FloatProvider bluntnessBase,
		@NotNull FloatProvider scaleBase
	) {
		return new LargeSpire(root, pointingUp, radius, bluntnessBase.sample(random), scaleBase.sample(random));
	}

	protected static boolean isCircleMostlyEmbeddedInStone(@NotNull WorldGenLevel level, @NotNull BlockPos pos, int radius) {
		if (isEmptyOrWaterOrLava(level, pos)) return false;
		float g = 6F / (float) radius;
		for (float h = 0F; h < 6.2831855F; h += g) {
			int i = (int) (Mth.cos(h) * (float) radius);
			int j = (int) (Mth.sin(h) * (float) radius);
			if (isEmptyOrWaterOrLava(level, pos.offset(i, 0, j))) return false;
		}
		return true;
	}

	protected static boolean isEmptyOrWaterOrLava(@NotNull LevelAccessor level, @NotNull BlockPos pos) {
		return level.isStateAtPosition(pos, LargeSpireFeature::isEmptyOrWaterOrLava);
	}

	public static boolean isEmptyOrWaterOrLava(@NotNull BlockState state) {
		return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
	}

	protected static double getHeight(double radius, double maxRadius, double scale, double minRadius) {
		if (radius < minRadius) radius = minRadius;

		double e = radius / maxRadius * 0.384D;
		double f = 0.75D * Math.pow(e, 1.3333333333333333D);
		double g = Math.pow(e, 0.6666666666666666D);
		double h = 0.3333333333333333D * Math.log(e);
		double i = scale * (f - g - h);
		i = Math.max(i, 0D);
		return i / 0.384D * maxRadius;
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<LargeSpireConfig> context) {
		WorldGenLevel worldGenLevel = context.level();
		BlockPos blockPos = context.origin();
		LargeSpireConfig largeSpireConfig = context.config();
		RandomSource randomSource = context.random();
		if (!LargeSpireFeature.isEmptyOrWaterOrLava(worldGenLevel, blockPos)) return false;
		Optional<Column> optional = Column.scan(
			worldGenLevel, blockPos,
			largeSpireConfig.floorToCeilingSearchRange,
			DripstoneUtils::isEmptyOrWaterOrLava,
			blockState -> LargeSpireFeature.isBaseOrLava(largeSpireConfig, blockState)
		);
		if (optional.isPresent() && optional.get() instanceof Column.Range range) {
			if (range.height() < 4) return false;
			int i = (int) ((float) range.height() * largeSpireConfig.maxColumnRadiusToCaveHeightRatio);
			int j = Mth.clamp(i, largeSpireConfig.columnRadius.getMinValue(), largeSpireConfig.columnRadius.getMaxValue());
			int k = Mth.randomBetweenInclusive(randomSource, largeSpireConfig.columnRadius.getMinValue(), j);
			LargeSpire largeSpire = make(blockPos.atY(range.ceiling() - 1), false, randomSource, k, largeSpireConfig.stalactiteBluntness, largeSpireConfig.heightScale);
			LargeSpire largeSpire2 = make(blockPos.atY(range.floor() + 1), true, randomSource, k, largeSpireConfig.stalagmiteBluntness, largeSpireConfig.heightScale);
			WindOffsetter windOffsetter;
			if (largeSpire.isSuitableForWind(largeSpireConfig) && largeSpire2.isSuitableForWind(largeSpireConfig)) {
				windOffsetter = new WindOffsetter(blockPos.getY(), randomSource, largeSpireConfig.windSpeed);
			} else {
				windOffsetter = WindOffsetter.noWind();
			}

			if (largeSpire.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(worldGenLevel, windOffsetter)) {
				largeSpire.placeBlocks(worldGenLevel, randomSource, windOffsetter, largeSpireConfig);
			}

			if (largeSpire2.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(worldGenLevel, windOffsetter)) {
				largeSpire2.placeBlocks(worldGenLevel, randomSource, windOffsetter, largeSpireConfig);
			}
			return true;
		}
		return false;
	}

	public static boolean isBaseOrLava(LargeSpireConfig config, BlockState blockState) {
		return isBase(config, blockState) || blockState.is(Blocks.LAVA);
	}

	public static boolean isBase(@NotNull LargeSpireConfig config, @NotNull BlockState blockState) {
		return blockState.is(config.baseBlocks) || blockState.is(config.replaceable);
	}

	static final class LargeSpire {
		private final boolean pointingUp;
		private final double bluntness;
		private final double scale;
		private BlockPos root;
		private int radius;

		LargeSpire(@NotNull BlockPos root, boolean pointingUp, int radius, double bluntness, double scale) {
			this.root = root;
			this.pointingUp = pointingUp;
			this.radius = radius;
			this.bluntness = bluntness;
			this.scale = scale;
		}

		private int getHeight() {
			return this.getHeightAtRadius(0F);
		}

		boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(@NotNull WorldGenLevel level, @NotNull WindOffsetter windOffsetter) {
			while (this.radius > 1) {
				BlockPos.MutableBlockPos mutableBlockPos = this.root.mutable();
				int i = Math.min(10, this.getHeight());

				for (int j = 0; j < i; ++j) {
					if (LargeSpireFeature.isCircleMostlyEmbeddedInStone(level, windOffsetter.offset(mutableBlockPos), this.radius)) {
						this.root = mutableBlockPos;
						return true;
					}
					mutableBlockPos.move(this.pointingUp ? Direction.DOWN : Direction.UP);
				}

				this.radius /= 2;
			}

			return false;
		}

		private int getHeightAtRadius(float radius) {
			return (int) LargeSpireFeature.getHeight(radius, this.radius, this.scale, this.bluntness);
		}

		void placeBlocks(@NotNull WorldGenLevel level, @NotNull RandomSource random, @NotNull WindOffsetter windOffsetter, @NotNull LargeSpireConfig config) {
			for (int i = -this.radius; i <= this.radius; ++i) {
				for (int j = -this.radius; j <= this.radius; ++j) {
					float f = Mth.sqrt((float) (i * i + j * j));
					if (!(f > (float) this.radius)) {
						int k = this.getHeightAtRadius(f);
						if (k > 0) {
							if ((double) random.nextFloat() < 0.2) {
								k = (int) ((float) k * Mth.randomBetween(random, 0.8F, 1.0F));
							}

							BlockPos.MutableBlockPos mutableBlockPos = this.root.offset(i, 0, j).mutable();
							boolean bl = false;
							int l = this.pointingUp ? level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutableBlockPos.getX(), mutableBlockPos.getZ()) : Integer.MAX_VALUE;

							for (int m = 0; m < k && mutableBlockPos.getY() < l; ++m) {
								BlockPos blockPos = windOffsetter.offset(mutableBlockPos);
								if (isEmptyOrWaterOrLava(level, blockPos)) {
									bl = true;
									level.setBlock(blockPos, config.pathBlock.getState(random, mutableBlockPos), Block.UPDATE_ALL);
								} else if (bl && level.getBlockState(blockPos).is(BlockTags.BASE_STONE_NETHER)) {
									break;
								}

								mutableBlockPos.move(this.pointingUp ? Direction.UP : Direction.DOWN);
							}
						}
					}
				}
			}
		}

		boolean isSuitableForWind(@NotNull LargeSpireConfig config) {
			return this.radius >= config.minRadiusForWind && this.bluntness >= (double) config.minBluntnessForWind;
		}
	}

	private static final class WindOffsetter {
		private final int originY;
		@Nullable
		private final Vec3 windSpeed;

		WindOffsetter(int originY, @NotNull RandomSource random, @NotNull FloatProvider magnitude) {
			this.originY = originY;
			float f = magnitude.sample(random);
			float g = Mth.randomBetween(random, 0F, 3.1415927F);
			this.windSpeed = new Vec3(Mth.cos(g) * f, 0D, Mth.sin(g) * f);
		}

		private WindOffsetter() {
			this.originY = 0;
			this.windSpeed = null;
		}

		@NotNull
		static WindOffsetter noWind() {
			return new WindOffsetter();
		}

		@NotNull
		BlockPos offset(@NotNull BlockPos pos) {
			if (this.windSpeed == null) return pos;
			Vec3 vec3 = this.windSpeed.scale(this.originY - pos.getY());
			return pos.offset(BlockPos.containing(vec3.x, 0D, vec3.z));
		}
	}
}


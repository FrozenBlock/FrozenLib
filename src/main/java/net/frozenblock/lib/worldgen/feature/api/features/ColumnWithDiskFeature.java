/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.frozenblock.lib.worldgen.feature.api.features.config.ColumnWithDiskFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class ColumnWithDiskFeature extends Feature<ColumnWithDiskFeatureConfig> {

	public ColumnWithDiskFeature(Codec<ColumnWithDiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<ColumnWithDiskFeatureConfig> context) {
		boolean generated = false;
		ColumnWithDiskFeatureConfig config = context.config();
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		BlockPos s = blockPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()) - 1);
		RandomSource random = level.getRandom();
		int radius = config.radius().sample(random);
		Optional<Holder<Block>> diskOptional = config.diskBlocks().getRandomElement(random);
		// DISK
		if (diskOptional.isPresent()) {
			BlockPos.MutableBlockPos mutableDisk = s.mutable();
			BlockState disk = diskOptional.get().value().defaultBlockState();
			int bx = s.getX();
			int bz = s.getZ();
			for (int x = bx - radius; x <= bx + radius; x++) {
				for (int z = bz - radius; z <= bz + radius; z++) {
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
					if (distance < radius * radius) {
						mutableDisk.set(x, level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
						boolean fade = !mutableDisk.closerThan(s, radius * 0.8);
						if (level.getBlockState(mutableDisk).is(config.replaceableBlocks())) {
							generated = true;
							if (fade) {
								if (random.nextFloat() > 0.65F) {
									level.setBlock(mutableDisk, disk, 3);
								}
							} else {
								level.setBlock(mutableDisk, disk, 3);
							}
						}
					}
				}
			}
		}
		// COLUMN
		BlockPos startPos = blockPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()) - 1);
		BlockState column = config.state();
		BlockPos.MutableBlockPos pos = startPos.mutable();
		for (int i = 0; i < config.height().sample(random); i++) {
			pos.set(pos.above());
			BlockState state = level.getBlockState(pos);
			if (level.getBlockState(pos.below()).is(Blocks.WATER)) {
				break;
			}
			if (state.getBlock() instanceof GrowingPlantBodyBlock || state.getBlock() instanceof BushBlock || state.isAir()) {
				level.setBlock(pos, column, 3);
				generated = true;
			}
		}
		startPos = startPos.offset(-1, 0, 0);
		generated = generated || place(config, level, random, startPos, column, pos);
		startPos = startPos.offset(1, 0, 1);
		generated = generated || place(config, level, random, startPos, column, pos);
		return generated;
	}

	private boolean place(@NotNull ColumnWithDiskFeatureConfig config, @NotNull WorldGenLevel level, RandomSource random, @NotNull BlockPos startPos, BlockState column, BlockPos.@NotNull MutableBlockPos pos) {
		boolean generated = false;
		pos.set(startPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, startPos.getX(), startPos.getZ()) - 1).mutable());
		for (int i = 0; i < config.additionalHeight().sample(random); i++) {
			pos.set(pos.above());
			BlockState state = level.getBlockState(pos);
			if (level.getBlockState(pos.below()).is(Blocks.WATER)) {
				break;
			}
			if (state.getBlock() instanceof GrowingPlantBodyBlock || state.getBlock() instanceof BushBlock || state.isAir()) {
				level.setBlock(pos, column, 3);
				generated = true;
			}
		}
		return generated;
	}

}

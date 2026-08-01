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

package net.frozenblock.lib.levelgen.feature.api.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record ColumnWithDiskFeature(
	BlockStateProvider state,
	IntProvider radius,
	IntProvider height,
	float surroundingPillarChance,
	HolderSet<Block> replaceableBlocks,
	BlockStateProvider diskState
) implements Feature {
	public static final MapCodec<ColumnWithDiskFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ColumnWithDiskFeature::state),
		IntProviders.NON_NEGATIVE_CODEC.fieldOf("radius").forGetter(ColumnWithDiskFeature::radius),
		IntProviders.NON_NEGATIVE_CODEC.fieldOf("height").forGetter(ColumnWithDiskFeature::height),
		Codec.FLOAT.fieldOf("surrounding_pillar_chance").forGetter(ColumnWithDiskFeature::surroundingPillarChance),
		RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("replaceable_blocks").forGetter(ColumnWithDiskFeature::replaceableBlocks),
		BlockStateProvider.CODEC.fieldOf("disk_block_state").forGetter(ColumnWithDiskFeature::diskState)
	).apply(instance, ColumnWithDiskFeature::new));

	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		final BlockPos surfacePos = origin.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ()) - 1);
		final int radius = this.radius.sample(random);

		boolean generated = false;

		// DISK
		placeDisk: {
			final BlockState diskState = this.diskState.getOptionalState(level, random, origin);
			if (diskState == null) break placeDisk;

			final BlockPos.MutableBlockPos mutable = surfacePos.mutable();
			final int originX = surfacePos.getX();
			final int originZ = surfacePos.getZ();
			for (int x = originX - radius; x <= originX + radius; x++) {
				for (int z = originZ - radius; z <= originZ + radius; z++) {
					final double distance = ((originX - x) * (originX - x) + ((originZ - z) * (originZ - z)));
					if (distance >= (radius * radius)) continue;

					mutable.set(x, level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
					final boolean fade = !mutable.closerThan(surfacePos, radius * 0.8D);
					if (!level.getBlockState(mutable).is(this.replaceableBlocks)) continue;

					generated = true;
					if (fade) {
						if (random.nextFloat() > 0.65F) level.setBlock(mutable, diskState, Block.UPDATE_CLIENTS);
					} else {
						level.setBlock(mutable, diskState, Block.UPDATE_CLIENTS);
					}
				}
			}
		}

		// COLUMN
		placeColumn: {
			final BlockState columnState = this.state.getState(level, random, origin);
			final BlockPos.MutableBlockPos mutable = origin.mutable();
			final int columnHeight = this.height.sample(random);

			generated = placeAtPos(level, origin, mutable, columnState, columnHeight) || generated;

			final int maxSurroundingPillarHeight = columnHeight - 1;
			if (maxSurroundingPillarHeight <= 0) break placeColumn;

			for (Direction direction : Direction.Plane.HORIZONTAL) {
				if (random.nextFloat() >= this.surroundingPillarChance) continue;
				generated = placeAtPos(level, origin.relative(direction), mutable, columnState, UniformInt.of(1, maxSurroundingPillarHeight).sample(random)) || generated;
			}
		}
		return generated;
	}

	private boolean placeAtPos(
		WorldGenLevel level,
		BlockPos pos,
		BlockPos.MutableBlockPos mutable,
		BlockState columnState,
		int height
	) {
		mutable.set(pos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1));
		if (!level.getBlockState(mutable).getFluidState().isEmpty()) return false;

		boolean generated = false;
		for (int i = 0; i < height; i++) {
			final BlockState state = level.getBlockState(mutable.move(Direction.UP));
			if (!state.canBeReplaced()) continue;

			level.setBlock(mutable, columnState, Block.UPDATE_CLIENTS);
			generated = true;
		}

		return generated;
	}
}

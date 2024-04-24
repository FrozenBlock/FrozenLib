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

package net.frozenblock.lib.entity.api.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BreatheAir<E extends PathfinderMob> extends Behavior<E> {
	public BreatheAir() {
		super(ImmutableMap.of());
	}

	@Override
	public boolean checkExtraStartConditions(ServerLevel level, E owner) {
		return super.checkExtraStartConditions(level, owner) && owner.getAirSupply() < 140;
	}

	@Override
	public boolean canStillUse(ServerLevel level, E owner, long gameTime) {
		return this.checkExtraStartConditions(level, owner);
	}

	@Override
	public void start(ServerLevel level, E entity, long gameTime) {
		this.findAirPosition(entity);
	}

	private void findAirPosition(@NotNull E entity) {
		Iterable<BlockPos> iterable = BlockPos.betweenClosed(
				Mth.floor(entity.getX() - 1.0),
				entity.getBlockY(),
				Mth.floor(entity.getZ() - 1.0),
				Mth.floor(entity.getX() + 1.0),
				Mth.floor(entity.getY() + 8.0),
				Mth.floor(entity.getZ() + 1.0)
		);
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : iterable) {
			if (this.givesAir(entity.level(), blockPos2)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos == null) {
			blockPos = BlockPos.containing(entity.getX(), entity.getY() + 8.0, entity.getZ());
		}

		entity.getNavigation().moveTo(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), 1.0);
	}

	@Override
	public void tick(ServerLevel level, E entity, long gameTime) {
		this.findAirPosition(entity);
		entity.moveRelative(0.02F, new Vec3(entity.xxa, entity.yya, entity.zza));
		entity.move(MoverType.SELF, entity.getDeltaMovement());
	}

	private boolean givesAir(@NotNull LevelReader level, @NotNull BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		return (level.getFluidState(pos).isEmpty() || blockState.is(Blocks.BUBBLE_COLUMN)) && blockState.isPathfindable(PathComputationType.LAND);
	}
}

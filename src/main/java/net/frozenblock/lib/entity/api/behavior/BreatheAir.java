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

/**
 * {@link net.minecraft.world.entity.ai.goal.BreathAirGoal} as a behavior.
 */
public class BreatheAir<E extends PathfinderMob> extends Behavior<E> {
	public BreatheAir() {
		super(ImmutableMap.of());
	}

	@Override
	public boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull E owner) {
		return super.checkExtraStartConditions(level, owner) && owner.getAirSupply() < 140;
	}

	@Override
	public boolean canStillUse(@NotNull ServerLevel level, @NotNull E owner, long gameTime) {
		return this.checkExtraStartConditions(level, owner);
	}

	@Override
	public void start(@NotNull ServerLevel level, @NotNull E entity, long gameTime) {
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
	public void tick(@NotNull ServerLevel level, @NotNull E entity, long gameTime) {
		this.findAirPosition(entity);
		entity.moveRelative(0.02F, new Vec3(entity.xxa, entity.yya, entity.zza));
		entity.move(MoverType.SELF, entity.getDeltaMovement());
	}

	private boolean givesAir(@NotNull LevelReader level, @NotNull BlockPos pos) {
		BlockState blockState = level.getBlockState(pos);
		return (level.getFluidState(pos).isEmpty() || blockState.is(Blocks.BUBBLE_COLUMN)) && blockState.isPathfindable(PathComputationType.LAND);
	}
}

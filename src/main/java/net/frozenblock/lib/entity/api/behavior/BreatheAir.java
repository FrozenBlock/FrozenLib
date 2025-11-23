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

/**
 * {@link net.minecraft.world.entity.ai.goal.BreathAirGoal} as a behavior.
 */
public class BreatheAir<E extends PathfinderMob> extends Behavior<E> {

	public BreatheAir() {
		super(ImmutableMap.of());
	}

	@Override
	public boolean checkExtraStartConditions(ServerLevel level, E entity) {
		return super.checkExtraStartConditions(level, entity) && entity.getAirSupply() < 140;
	}

	@Override
	public boolean canStillUse(ServerLevel level, E entity, long gameTime) {
		return this.checkExtraStartConditions(level, entity);
	}

	@Override
	public void start(ServerLevel level, E entity, long gameTime) {
		this.findAirPosition(entity);
	}

	private void findAirPosition(E entity) {
		final Iterable<BlockPos> poses = BlockPos.betweenClosed(
			Mth.floor(entity.getX() - 1),
			entity.getBlockY(),
			Mth.floor(entity.getZ() - 1),
			Mth.floor(entity.getX() + 1),
			Mth.floor(entity.getY() + 8),
			Mth.floor(entity.getZ() + 1)
		);
		BlockPos pos = null;

		for (BlockPos searchingPos : poses) {
			if (!this.givesAir(entity.level(), searchingPos)) continue;
			pos = searchingPos;
			break;
		}

		if (pos == null) pos = BlockPos.containing(entity.getX(), entity.getY() + 8, entity.getZ());
		entity.getNavigation().moveTo(pos.getX(), pos.getY() + 1, pos.getZ(), 1);
	}

	@Override
	public void tick(ServerLevel level, E entity, long gameTime) {
		this.findAirPosition(entity);
		entity.moveRelative(0.02F, new Vec3(entity.xxa, entity.yya, entity.zza));
		entity.move(MoverType.SELF, entity.getDeltaMovement());
	}

	private boolean givesAir(LevelReader level, BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		return (state.getFluidState().isEmpty() || state.is(Blocks.BUBBLE_COLUMN)) && state.isPathfindable(PathComputationType.LAND);
	}
}

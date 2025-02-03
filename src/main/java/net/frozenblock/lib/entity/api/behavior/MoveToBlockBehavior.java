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
import net.frozenblock.lib.entity.impl.behavior.FrozenLibBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

/**
 * {@link MoveToBlockGoal} as a behavior.
 */
public abstract class MoveToBlockBehavior<E extends PathfinderMob> extends Behavior<E> {
	public static final int DURATION = 1200;
	public final double speedModifier;
	protected final E mob;
	private final int searchRange;
	private final int verticalSearchRange;
	protected int tryTicks;
	protected BlockPos blockPos = BlockPos.ZERO;
	protected int verticalSearchStart;
	private boolean reachedTarget;

	public MoveToBlockBehavior(E mob, double speedModifier, int searchRange) {
		this(mob, speedModifier, searchRange, 1);
	}

	public MoveToBlockBehavior(E mob, double speedModifier, int searchRange, int verticalSearchRange) {
		super(ImmutableMap.of(), DURATION);
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.searchRange = searchRange;
		this.verticalSearchStart = 0;
		this.verticalSearchRange = verticalSearchRange;
	}

	@Override
	public boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull E owner) {
		return this.findNearestBlock();
	}

	@Override
	public boolean canStillUse(@NotNull ServerLevel level, @NotNull E entity, long gameTime) {
		return this.tryTicks >= -((FrozenLibBehavior) this).frozenLib$getDuration() && this.tryTicks <= DURATION && this.isValidTarget(level, this.blockPos);
	}

	@Override
	public void start(@NotNull ServerLevel level, @NotNull E entity, long gameTime) {
		this.moveMobToBlock();
		this.tryTicks = 0;
	}

	protected void moveMobToBlock() {
		this.mob
			.getNavigation()
			.moveTo(this.blockPos.getX() + 0.5D, this.blockPos.getY() + 1D, this.blockPos.getZ() + 0.5D, this.speedModifier);
	}

	public double acceptedDistance() {
		return 1D;
	}

	protected BlockPos getMoveToTarget() {
		return this.blockPos.above();
	}

	@Override
	protected void tick(@NotNull ServerLevel level, @NotNull E owner, long gameTime) {
		BlockPos blockPos = this.getMoveToTarget();
		if (!blockPos.closerToCenterThan(owner.position(), this.acceptedDistance())) {
			this.reachedTarget = false;
			++this.tryTicks;
			if (this.shouldRecalculatePath()) {
				this.mob
					.getNavigation()
					.moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, this.speedModifier);
			}
		} else {
			this.reachedTarget = true;
			--this.tryTicks;
		}
	}

	public boolean shouldRecalculatePath() {
		return this.tryTicks % 40 == 0;
	}

	protected boolean isReachedTarget() {
		return this.reachedTarget;
	}

	/**
	 * Searches and sets new destination block and returns true if a suitable block (specified in {@link #isValidTarget(LevelReader, BlockPos)}) can be found.
	 */
	protected boolean findNearestBlock() {
		BlockPos blockPos = this.mob.blockPosition();
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

		for (int k = this.verticalSearchStart; k <= this.verticalSearchRange; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < this.searchRange; ++l) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						mutableBlockPos.setWithOffset(blockPos, m, k - 1, n);
						if (this.mob.isWithinRestriction(mutableBlockPos) && this.isValidTarget(this.mob.level(), mutableBlockPos)) {
							this.blockPos = mutableBlockPos;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * Return true to set given position as destination.
	 */
	public abstract boolean isValidTarget(LevelReader level, BlockPos pos);
}
